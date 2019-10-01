package ru.iqsolution.tkoonline.services.workers

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.remote.api.RequestClean
import ru.iqsolution.tkoonline.services.BaseWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SendWorker(app: Application, params: WorkerParameters) : BaseWorker(app, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    val server: Server by instance()

    override suspend fun doWork(): Result = coroutineScope {
        val plaintText = "text/plain".toMediaTypeOrNull()
        db.cleanDao().getEvents().reversed().forEach {
            try {
                val responseClean = server.sendClean(
                    it.token.authHeader,
                    it.clean.kpId,
                    RequestClean(it.clean)
                ).execute()
                Timber.d("responseClean ${responseClean.code()}")
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        db.photoDao().getEvents().reversed().forEach {
            try {
                fileManager.readFile(it.photo.path)?.let { photo ->
                    val responsePhoto = server.sendPhoto(
                        it.token.authHeader,
                        it.photo.kpId?.toString()?.toRequestBody(plaintText),
                        it.photo.typeId.toString().toRequestBody(plaintText),
                        it.photo.whenTime.toString(PATTERN_DATETIME).toRequestBody(plaintText),
                        it.photo.latitude.toString().toRequestBody(plaintText),
                        it.photo.longitude.toString().toRequestBody(plaintText),
                        photo
                    ).execute()
                    Timber.d("responsePhoto ${responsePhoto.code()}")
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        Result.success()
    }

    companion object {

        fun launch(context: Context): LiveData<WorkInfo> {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.METERED)
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