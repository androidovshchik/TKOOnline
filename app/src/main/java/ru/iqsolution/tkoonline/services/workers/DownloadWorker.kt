@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.extensions.cancelAll
import ru.iqsolution.tkoonline.local.FileManager
import timber.log.Timber
import java.io.File

class DownloadWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val fileManager: FileManager by instance()

    private val client: OkHttpClient by instance()

    override fun doWork(): Result {
        val url = inputData.getString(PARAM_URL) ?: return Result.failure()
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
                    val result = fileManager.writeFile(File(fileManager.externalDir, "app.apk")) {
                        it.write(body.bytes())
                        it.flush()
                    }
                    return if (result) {
                        Result.success()
                    } else {
                        Result.failure()
                    }
                }
            }
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return Result.failure()
    }

    override fun onStopped() {
        val url = inputData.getString(PARAM_URL)
        client.cancelAll(url)
    }

    companion object {

        private const val NAME = "DOWNLOAD"

        private const val PARAM_URL = "url"

        fun launch(context: Context, url: String): LiveData<WorkInfo> {
            val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(PARAM_URL, url)
                        .build()
                )
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