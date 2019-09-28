package ru.iqsolution.tkoonline.services.workers

import android.app.Application
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.AppDatabase
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.services.BaseWorker

class DeleteWorker(app: Application, params: WorkerParameters) : BaseWorker(app, params) {

    val appDb: AppDatabase by instance()

    val fileManager: FileManager by instance()

    override suspend fun doWork(): Result = coroutineScope {
        fileManager.deleteOldFiles()
        Result.success()
    }
}