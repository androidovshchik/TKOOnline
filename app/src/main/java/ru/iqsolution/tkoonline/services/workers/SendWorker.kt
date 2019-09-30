package ru.iqsolution.tkoonline.services.workers

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.remote.api.RequestClean
import ru.iqsolution.tkoonline.services.BaseWorker
import java.util.concurrent.TimeUnit

class SendWorker(app: Application, params: WorkerParameters) : BaseWorker(app, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    val server: Server by instance()

    override suspend fun doWork(): Result = coroutineScope {
        db.cleanDao().getEvents().reversed().chunked(2).forEach {
            val a = async {
                val event1 = it[0]
                val event2 = it.getOrNull(1)
                val request = RequestClean(event1.cleaning)
                event1?.token?.token
                server.sendClean("", 0, RequestClean(event1.cleaning)).execute()

            }
            val b = async {

            }
            launch {

            }
            awaiaat()
        }
        db.photoDao().getEvents().reversed().chunked(2).forEach {
            server.sendPhoto().execute()
        }
        Result.success()
    }

    companion object {

        fun launch(context: Context): LiveData<WorkInfo> {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<SendWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork("DELETE", ExistingWorkPolicy.KEEP, request)
                WorkManager.getInstance(context)
                    .enqueue(request)
                return getWorkInfoByIdLiveData(work.id)
            }
        }
    }
}