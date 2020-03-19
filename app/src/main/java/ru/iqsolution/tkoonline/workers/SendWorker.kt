@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.workers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.anko.connectivityManager
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.ACTION_CLOUD
import ru.iqsolution.tkoonline.exitUnexpected
import ru.iqsolution.tkoonline.extensions.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.extensions.bgToast
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

    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    override fun doWork(): Result {
        var hasErrors = false
        val send = inputData.getBoolean(PARAM_SEND, true)
        val exit = inputData.getBoolean(PARAM_EXIT, false)
        val output = mapOf(PARAM_SEND to send)
        if (!applicationContext.connectivityManager.isConnected) {
            return failure(output)
        }
        val header = preferences.authHeader
        if (send) {
            val cleanEvents = db.cleanDao().getSendEvents()
            cleanEvents.forEach { event ->
                try {
                    val response = server.sendClean(
                        event.token.authHeader,
                        event.clean.kpId,
                        RequestClean(event.clean)
                    ).execute()
                    val code = response.code()
                    when {
                        response.isSuccessful -> db.cleanDao()
                            .markAsSent(event.clean.id ?: return@forEach)
                        code == 400 || code == 401 -> {
                            val errors = response.parseErrors()
                            val codes = errors.map { it.code }
                            if (codes.contains("closed token")) {
                                if (event.token.authHeader == header) {
                                    applicationContext.run {
                                        bgToast("Ваша авторизация сброшена, пожалуйста, авторизуйтесь заново")
                                        exitUnexpected()
                                    }
                                    return success(output)
                                }
                            } else {
                                hasErrors = true
                            }
                        }
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
            photoEvents.forEach { event ->
                val photo = fileManager.readFile(event.photo.path) ?: return@forEach
                try {
                    val response = server.sendPhoto(
                        event.token.authHeader,
                        event.photo.kpId?.toString()?.toRequestBody(TEXT_TYPE),
                        event.photo.typeId.toString().toRequestBody(TEXT_TYPE),
                        event.photo.whenTime.toString(PATTERN_DATETIME_ZONE)
                            .toRequestBody(TEXT_TYPE),
                        event.photo.latitude.toString().toRequestBody(TEXT_TYPE),
                        event.photo.longitude.toString().toRequestBody(TEXT_TYPE),
                        photo
                    ).execute()
                    val code = response.code()
                    when {
                        response.isSuccessful -> db.photoDao()
                            .markAsSent(event.photo.id ?: return@forEach)
                        code == 400 || code == 401 -> {
                            val errors = response.parseErrors()
                            val codes = errors.map { it.code }
                            if (codes.contains("closed token")) {
                                if (event.token.authHeader == header) {
                                    applicationContext.run {
                                        bgToast("Ваша авторизация сброшена, пожалуйста, авторизуйтесь заново")
                                        exitUnexpected()
                                    }
                                    return success(output)
                                }
                            } else {
                                hasErrors = true
                            }
                        }
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
        }
        if (!send || !hasErrors) {
            if (exit) {
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

        fun launch(ctx: Context, send: Boolean = true, exit: Boolean = false): LiveData<WorkInfo>? {
            val request = OneTimeWorkRequestBuilder<SendWorker>()
                .setInputData(
                    Data.Builder()
                        .putBoolean(PARAM_SEND, send)
                        .putBoolean(PARAM_EXIT, exit)
                        .build()
                )
                .apply {
                    if (exit) {
                        setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            WorkRequest.MIN_BACKOFF_MILLIS,
                            TimeUnit.MILLISECONDS
                        )
                    }
                }
                .build()
            WorkManager.getInstance(ctx).apply {
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