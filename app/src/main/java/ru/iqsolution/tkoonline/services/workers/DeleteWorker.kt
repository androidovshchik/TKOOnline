package ru.iqsolution.tkoonline.services.workers

import android.app.Application
import android.content.Context
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.services.BaseWorker
import java.util.*
import java.util.concurrent.TimeUnit

class DeleteWorker(app: Application, params: WorkerParameters) : BaseWorker(app, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    override suspend fun doWork(): Result = coroutineScope {
        val now = DateTime.now()
        val timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
        val expiredTokens = arrayListOf<AccessToken>()
        db.tokenDao().apply {
            getTokens().forEach {
                if (it.expires.withZone(timeZone).isBefore(now)) {
                    expiredTokens.add(it)
                }
            }
            if (expiredTokens.isNotEmpty()) {
                delete(expiredTokens)
            }
        }
        fileManager.deleteOldFiles()
        Result.success()
    }

    companion object {

        fun launch(context: Context) {
            val request = OneTimeWorkRequestBuilder<DeleteWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MAX_BACKOFF_MILLIS + 1,
                    TimeUnit.MILLISECONDS
                )
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork("DELETE", ExistingWorkPolicy.KEEP, request)
            }
        }
    }
}