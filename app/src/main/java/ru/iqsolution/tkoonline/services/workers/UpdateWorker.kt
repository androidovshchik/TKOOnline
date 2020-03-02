@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller.SessionParams
import androidx.lifecycle.LiveData
import androidx.work.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.connectivityManager
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.extensions.isConnected
import ru.iqsolution.tkoonline.extensions.pendingReceiverFor
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.services.AdminManager
import timber.log.Timber
import java.util.concurrent.TimeUnit

class UpdateWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val fileManager: FileManager by instance()

    private val client: OkHttpClient by instance()

    private val adminManager: AdminManager by instance()

    override fun doWork(): Result {
        val url = inputData.getString(PARAM_URL) ?: return Result.failure()
        if (!applicationContext.connectivityManager.isConnected) {
            return Result.failure()
        }
        try {
            val request = Request.Builder()
                .url(url)
                .get()
                .tag(url)
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body
                if (body != null) {
                    if (body.contentType()?.type != "application") {
                        Timber.e("Invalid mime type of apk ${body.contentType()}")
                        return Result.failure()
                    }
                    if (adminManager.isDeviceOwner) {
                        val packageName = applicationContext.packageName
                        val packageInstaller = applicationContext.packageManager.packageInstaller
                        val params = SessionParams(SessionParams.MODE_FULL_INSTALL)
                        params.setAppPackageName(packageName)
                        val sessionId = packageInstaller.createSession(params)
                        packageInstaller.openSession(sessionId).use {
                            it.openWrite(packageName, 0, -1).use { output ->
                                body.byteStream().copyTo(output)
                                it.fsync(output)
                            }
                            it.commit(
                                applicationContext.pendingReceiverFor(Intent.ACTION_PACKAGE_ADDED, sessionId)
                                    .intentSender
                            )
                        }
                        return Result.success()
                    } else {
                        val apkFile = fileManager.apkFile
                        if (apkFile != null) {
                            val hasWritten = fileManager.writeFile(apkFile) {
                                body.byteStream().copyTo(it)
                                it.flush()
                            }
                            if (hasWritten) {
                                return Result.success()
                            }
                        } else {
                            return Result.failure()
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Timber.e(e)
            fileManager.deleteFile(fileManager.apkFile)
        }
        return if (runAttemptCount >= 2) Result.failure() else Result.retry()
    }

    companion object {

        private const val NAME = "UPDATE"

        private const val PARAM_URL = "url"

        fun launch(context: Context, url: String): LiveData<WorkInfo> {
            val request = OneTimeWorkRequestBuilder<UpdateWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(PARAM_URL, url)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(NAME, ExistingWorkPolicy.REPLACE, request)
                return getWorkInfoByIdLiveData(request.id)
            }
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).apply {
                cancelUniqueWork(NAME)
            }
        }
    }
}