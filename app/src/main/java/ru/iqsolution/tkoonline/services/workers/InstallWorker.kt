@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.FileManager
import timber.log.Timber
import java.io.File

class InstallWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val fileManager: FileManager by instance()

    private val client: OkHttpClient by instance()

    override fun doWork(): Result {
        val url = inputData.getString(PARAM_URL) ?: return Result.success()
        try {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            val body = client.newCall(request).execute().body
            if (body != null) {
                fileManager.writeFile(File("")) {
                    it.write(body.bytes())
                    it.flush()
                }
            }
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return Result.success()
    }

    override fun onStopped() {

    }

    companion object {

        private const val NAME = "INSTALL"

        private const val PARAM_URL = "url"

        fun launch(context: Context, url: String): LiveData<WorkInfo> {
            val request = OneTimeWorkRequestBuilder<InstallWorker>()
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