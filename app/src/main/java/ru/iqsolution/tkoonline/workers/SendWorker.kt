@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.workers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.anko.connectivityManager
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.ACTION_CLOUD
import ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.extensions.isConnected
import ru.iqsolution.tkoonline.extensions.parseErrors
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

    private val server: Server by instance()

    private val gson: Gson by instance(arg = false)

    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    override fun doWork(): Result {
        var hasErrors = false
        val send = inputData.getBoolean(PARAM_SEND, true)
        val exit = inputData.getBoolean(PARAM_EXIT, false)
        val output = mapOf(PARAM_SEND to send)
        if (!applicationContext.connectivityManager.isConnected) {
            return failure(output)
        }
        if (send) {
            val cleanEvents = db.cleanDao().getSendEvents()
            cleanEvents.forEach {
                try {
                    val response = server.sendClean(
                        it.token.authHeader,
                        it.clean.kpId,
                        RequestClean(it.clean)
                    ).execute()
                    val code = response.code()
                    when {
                        response.isSuccessful -> db.cleanDao().markAsSent(it.clean.id ?: 0L)
                        code == 401 || code == 403 -> return success(output)
                        else -> {
                            val errors = response.parseErrors(gson)
                            if (!errors.contains("closed token")) {
                                hasErrors = true
                            }
                        }
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
                    val response = server.sendPhoto(
                        it.token.authHeader,
                        it.photo.kpId?.toString()?.toRequestBody(TEXT_TYPE),
                        it.photo.typeId.toString().toRequestBody(TEXT_TYPE),
                        it.photo.whenTime.toString(PATTERN_DATETIME_ZONE).toRequestBody(TEXT_TYPE),
                        it.photo.latitude.toString().toRequestBody(TEXT_TYPE),
                        it.photo.longitude.toString().toRequestBody(TEXT_TYPE),
                        photo
                    ).execute()
                    val code = response.code()
                    when {
                        response.isSuccessful -> db.photoDao().markAsSent(it.photo.id ?: 0L)
                        code == 401 || code == 403 -> return success(output)
                        else -> {
                            val errors = response.parseErrors(gson)
                            if (!errors.contains("closed token")) {
                                hasErrors = true
                            }
                        }
                    }
                } catch (e: Throwable) {
                    Timber.e(e)
                    hasErrors = true
                }
            }
            if (photoEvents.isNotEmpty()) {
                broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
            }
        }
        if (!send || !hasErrors) {
            if (exit) {
                val header = preferences.authHeader
                if (header != null) {
                    try {
                        server.logout(header).execute()
                    } catch (e: Throwable) {
                        Timber.e(e)
                        hasErrors = true
                    }
                }
            }
        }
        return when {
            hasErrors -> if (runAttemptCount >= 2) failure(output) else Result.retry()
            else -> success(output)
        }
    }

    companion object {

        private const val NAME = "SEND"

        const val PARAM_SEND = "send"

        private const val PARAM_EXIT = "exit"

        private val TEXT_TYPE = "text/plain".toMediaTypeOrNull()

        fun launch(context: Context, send: Boolean = true, exit: Boolean = false): LiveData<WorkInfo>? {
            val request = OneTimeWorkRequestBuilder<SendWorker>()
                .setInputData(
                    Data.Builder()
                        .putBoolean(PARAM_SEND, send)
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