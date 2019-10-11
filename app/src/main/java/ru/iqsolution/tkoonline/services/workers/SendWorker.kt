package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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

class SendWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    val preferences: Preferences by instance()

    val server: Server by instance()

    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    override suspend fun doWork(): Result = coroutineScope {
        val retry = inputData.getBoolean(PARAM_RETRY, false)
        var hasErrors = false
        require(!isStopped)
        val cleanEvents = db.cleanDao().getSendEvents()
        cleanEvents.forEach {
            require(!isStopped)
            try {
                val responseClean = server.sendClean(
                    it.token.authHeader,
                    it.clean.kpId,
                    RequestClean(it.clean)
                ).execute()
                require(!isStopped)
                when {
                    responseClean.code() in 200..299 -> db.cleanDao().markAsSent(it.clean.id ?: 0L)
                    responseClean.code() == 401 -> return@coroutineScope Result.success()
                }
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = true
            }
        }
        require(!isStopped)
        if (cleanEvents.isNotEmpty()) {
            broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
        }
        require(!isStopped)
        val photoEvents = db.photoDao().getSendEvents()
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
                when {
                    responsePhoto.code() in 200..299 -> db.photoDao().markAsSent(it.photo.id ?: 0L)
                    responsePhoto.code() == 401 -> return@coroutineScope Result.success()
                }
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = true
            }
        }
        require(!isStopped)
        if (photoEvents.isNotEmpty()) {
            broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
        }
        if (retry) {
            require(!isStopped)
            val locationCount = db.locationDao().getSendCount()
            if (locationCount > 0) {
                // awaiting telemetry service
                return@coroutineScope Result.retry()
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

    override val kodein = MainApp.instance.kodein

    companion object {

        private const val NAME = "SEND"

        private const val PARAM_RETRY = "retry"

        private const val PARAM_PHOTO_NO_KP = "photo_no_kp"

        private val TEXT_TYPE = "text/plain".toMediaTypeOrNull()

        fun launch(context: Context, retry: Boolean = false): LiveData<WorkInfo>? {
            val request = OneTimeWorkRequestBuilder<SendWorker>()
                .setInputData(
                    Data.Builder()
                        .putBoolean(PARAM_RETRY, retry)
                        .build()
                )
                .apply {
                    if (retry) {
                        setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                    }
                }
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(NAME, ExistingWorkPolicy.REPLACE, request)
                return if (retry) {
                    getWorkInfoByIdLiveData(request.id)
                } else {
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