package ru.iqsolution.tkoonline.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import org.kodein.di.instance
import ru.iqsolution.tkoonline.isEarlier
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.Token

class DeleteWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val db: Database by instance()

    private val fileManager: FileManager by instance()

    override fun doWork(): Result {
        fileManager.deleteOldFiles()
        val allTokens = db.tokenDao().getAll()
        val expiredTokens = mutableListOf<Token>()
        for (token in allTokens) {
            if (token.expires.isEarlier()) {
                expiredTokens.add(token)
            } else {
                break
            }
        }
        if (expiredTokens.isNotEmpty()) {
            db.tokenDao().delete(expiredTokens)
        }
        return Result.success()
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