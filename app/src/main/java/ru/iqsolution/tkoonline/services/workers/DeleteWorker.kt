package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.services.BaseWorker

class DeleteWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    override suspend fun doWork(): Result = coroutineScope {
        fileManager.deleteOldFiles()
        val now = DateTime.now()
        val allTokens = db.tokenDao().getTokens()
        val expiredTokens = arrayListOf<AccessToken>()
        for (token in allTokens) {
            if (token.expires.withZone(now.zone).isBefore(now)) {
                expiredTokens.add(token)
            } else {
                break
            }
        }
        if (expiredTokens.isNotEmpty()) {
            db.tokenDao().delete(expiredTokens)
        }
        Result.success()
    }

    companion object {

        private const val NAME = "DELETE"

        fun launch(context: Context) {
            val request = OneTimeWorkRequestBuilder<DeleteWorker>()
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(NAME, ExistingWorkPolicy.KEEP, request)
            }
        }
    }
}