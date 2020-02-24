@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.anko.connectivityManager
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.ACTION_CLOUD
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.extensions.isConnected
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.remote.api.RequestClean
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SendWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val db: Database by instance()

    private val fileManager: FileManager by instance()

    private val preferences: Preferences by instance()

    private val client: OkHttpClient by instance()

    private val server: Server by instance()

    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    override fun doWork(): Result {
        var hasErrors = false
        val exit = inputData.getBoolean(PARAM_EXIT, false)
        if (!applicationContext.connectivityManager.isConnected) {
            return Result.failure()
        }
        val cleanEvents = db.cleanDao().getSendEvents()
        cleanEvents.forEach {
            try {
                val responseClean = server.sendClean(
                    it.token.authHeader,
                    it.clean.kpId,
                    RequestClean(it.clean)
                ).execute()
                when {
                    responseClean.isSuccessful -> db.cleanDao().markAsSent(it.clean.id ?: 0L)
                    responseClean.code() == 401 -> return Result.success()
                    else -> hasErrors = true
                }
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = true
            }
        }
        if (cleanEvents.isNotEmpty()) {
            broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
        }
        val photoEvents = db.photoDao().getSendEvents()
        photoEvents.forEach {
            val photo = fileManager.readFile(it.photo.path) ?: return@forEach
            try {
                val responsePhoto = server.sendPhoto(
                    it.token.authHeader,
                    it.photo.kpId?.toString()?.toRequestBody(TEXT_TYPE),
                    it.photo.typeId.toString().toRequestBody(TEXT_TYPE),
                    it.photo.whenTime.toString(PATTERN_DATETIME).toRequestBody(TEXT_TYPE),
                    it.photo.latitude.toString().toRequestBody(TEXT_TYPE),
                    it.photo.longitude.toString().toRequestBody(TEXT_TYPE),
                    photo
                ).execute()
                when {
                    responsePhoto.isSuccessful -> db.photoDao().markAsSent(it.photo.id ?: 0L)
                    responsePhoto.code() == 401 -> return Result.success()
                    else -> hasErrors = true
                }
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = true
            }
        }
        if (photoEvents.isNotEmpty()) {
            broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
        }
        if (exit) {
            val locationCount = db.locationDao().getSendCount()
            if (locationCount > 0) {
                // awaiting telemetry service
                return if (runAttemptCount >= 2) Result.failure() else Result.retry()
            }
            try {
                server.logout(preferences.authHeader).execute()
            } catch (e: Throwable) {
                Timber.e(e)
                hasErrors = true
            }
        }
        return when {
            hasErrors -> if (runAttemptCount >= 2) Result.failure() else Result.retry()
            else -> Result.success()
        }
    }

    companion object {

        private const val NAME = "SEND"

        private const val PARAM_EXIT = "exit"

        private val TEXT_TYPE = "text/plain".toMediaTypeOrNull()

        fun launch(context: Context, exit: Boolean = false): LiveData<WorkInfo>? {
            val request = OneTimeWorkRequestBuilder<SendWorker>()
                .setInputData(
                    Data.Builder()
                        .putBoolean(PARAM_EXIT, exit)
                        .build()
                )
                .apply {
                    if (exit) {
                        setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                    }
                }
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(NAME, ExistingWorkPolicy.REPLACE, request)
                return if (exit) {
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