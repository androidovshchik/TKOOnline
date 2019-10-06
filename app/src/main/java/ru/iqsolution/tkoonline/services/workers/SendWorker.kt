package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.remote.api.RequestClean
import ru.iqsolution.tkoonline.services.BaseWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SendWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    val preferences: Preferences by instance()

    val server: Server by instance()

    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    override suspend fun doWork(): Result = coroutineScope {
        val kpId = inputData.getInt(PARAM_KP_ID, -1)
        val photoNoKp = inputData.getBoolean(PARAM_PHOTO_NO_KP, false)
        val sendAll = kpId < 0 && !photoNoKp
        var hasErrors = false
        if (!photoNoKp) {
            val cleanEvents = if (sendAll) {
                db.cleanDao().getSendEvents()
            } else {
                db.cleanDao().getSendKpIdEvents(kpId)
            }
            cleanEvents.forEach {
                try {
                    val responseClean = server.sendClean(
                        it.token.authHeader,
                        it.clean.kpId,
                        RequestClean(it.clean)
                    ).execute()
                    if (responseClean.code() in 200..299) {
                        db.cleanDao().markAsSent(it.clean.id ?: 0L)
                    }
                } catch (e: Throwable) {
                    Timber.e(e)
                    hasErrors = sendAll
                }
            }
        }
        val photoEvents = when {
            onlyPhoto -> db.photoDao().getSendPhotoEvents()
            sendAll -> db.photoDao().getSendEvents()
            else -> db.photoDao().getSendKpIdEvents(kpId)
        }
        photoEvents.forEach {
            val photo = fileManager.readFile(it.photo.path) ?: return@forEach
            try {
                val responsePhoto = server.sendPhoto(
                    it.token.authHeader,
                    it.photo.kpId?.toString()?.toRequestBody(TEXT_TYPE),
                    it.photo.type.toString().toRequestBody(TEXT_TYPE),
                    it.photo.whenTime.toString(PATTERN_DATETIME).toRequestBody(TEXT_TYPE),
                    it.photo.latitude.toString().toRequestBody(TEXT_TYPE),
                    it.photo.longitude.toString().toRequestBody(TEXT_TYPE),
                    photo
                ).execute()
                if (responsePhoto.code() in 200..299) {
                    db.cleanDao().markAsSent(it.photo.id ?: 0L)
                }
                // todo send broadcast
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = sendAll
            }
        }
        if (sendAll) {
            try {
                server.logout(preferences.authHeader).execute()
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = true
            }
            if (hasErrors) {
                Result.retry()
            } else {
                Result.success()
            }
        } else {
            Result.success()
        }
    }

    companion object {

        private const val NAME = "SEND"

        private const val PARAM_KP_ID = "kp_id"

        private const val PARAM_PHOTO_NO_KP = "photo_no_kp"

        private val TEXT_TYPE = "text/plain".toMediaTypeOrNull()

        fun launch(context: Context, kp: Int = -1, photoNoKp: Boolean = false): LiveData<WorkInfo>? {
            val sendAll = kp < 0 && !photoNoKp
            val request = OneTimeWorkRequestBuilder<SendWorker>()
                .setInputData(
                    Data.Builder()
                        .putInt(PARAM_KP_ID, kp)
                        .putBoolean(PARAM_PHOTO_NO_KP, photoNoKp)
                        .build()
                )
                .apply {
                    if (sendAll) {
                        setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                    }
                }
                .build()
            WorkManager.getInstance(context).apply {
                return if (sendAll) {
                    enqueueUniqueWork(NAME, ExistingWorkPolicy.REPLACE, request)
                    getWorkInfoByIdLiveData(request.id)
                } else {
                    enqueueUniqueWork(NAME, ExistingWorkPolicy.KEEP, request)
                    null
                }
            }
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).apply {
                cancelUniqueWork(NAME)
            }
        }
    }
}