package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.ACTION_CLOUD
import ru.iqsolution.tkoonline.MainApp
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.remote.api.RequestClean
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SendWorker(context: Context, params: WorkerParameters) : Worker(context, params), KodeinAware {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    val preferences: Preferences by instance()

    val client: OkHttpClient by instance()

    val server: Server by instance()

    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    override fun doWork(): Result {
        val kpId = inputData.getInt(PARAM_KP_ID, -1)
        val photoNoKp = inputData.getBoolean(PARAM_PHOTO_NO_KP, false)
        val sendAll = kpId < 0 && !photoNoKp
        var hasErrors = false
        require(!isStopped)
        if (!photoNoKp) {
            val cleanEvents = if (sendAll) {
                db.cleanDao().getSendEvents()
            } else {
                db.cleanDao().getSendKpIdEvents(kpId)
            }
            cleanEvents.forEach {
                require(!isStopped)
                try {
                    val responseClean = server.sendClean(
                        it.token.authHeader,
                        it.clean.kpId,
                        RequestClean(it.clean)
                    ).execute()
                    require(!isStopped)
                    if (responseClean.code() in 200..299) {
                        db.cleanDao().markAsSent(it.clean.id ?: 0L)
                    }
                } catch (e: Throwable) {
                    Timber.e(e)
                    hasErrors = sendAll
                }
            }
            require(!isStopped)
            if (cleanEvents.isNotEmpty()) {
                broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
            }
        }
        require(!isStopped)
        val photoEvents = when {
            photoNoKp -> db.photoDao().getSendNoKpEvents()
            sendAll -> db.photoDao().getSendEvents()
            else -> db.photoDao().getSendKpIdEvents(kpId)
        }
        photoEvents.forEach {
            require(!isStopped)
            val photo = fileManager.readFile(it.photo.path) ?: return@forEach
            require(!isStopped)
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
                require(!isStopped)
                if (responsePhoto.code() in 200..299) {
                    db.photoDao().markAsSent(it.photo.id ?: 0L)
                }
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = sendAll
            }
        }
        require(!isStopped)
        if (photoEvents.isNotEmpty()) {
            broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
        }
        return if (sendAll) {
            require(!isStopped)
            val locationCount = db.locationDao().getSendCount()
            if (locationCount > 0) {
                // awaiting telemetry service
                return Result.retry()
            }
            require(!isStopped)
            try {
                server.logout(preferences.authHeader).execute()
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = true
            }
            when {
                hasErrors -> Result.retry()
                isStopped -> Result.failure()
                else -> Result.success()
            }
        } else {
            when {
                isStopped -> Result.failure()
                else -> Result.success()
            }
        }
    }

    override fun onStopped() {
        super.onStopped()
        client.dispatcher.cancelAll()
    }

    override val kodein = MainApp.instance.kodein

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