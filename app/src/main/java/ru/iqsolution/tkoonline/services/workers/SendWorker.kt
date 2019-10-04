package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.EXTRA_WORKER_MESSAGE
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.remote.api.RequestClean
import ru.iqsolution.tkoonline.services.BaseWorker
import timber.log.Timber

class SendWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    val preferences: Preferences by instance()

    val server: Server by instance()

    override suspend fun doWork(): Result = coroutineScope {
        val kpId = inputData.getInt(PARAM_KP_ID, -1)
        val plaintText = "text/plain".toMediaTypeOrNull()
        val cleanEvents = if (kpId < 0) {
            db.cleanDao().getSendEvents()
        } else {
            db.cleanDao().getSendKpIdEvents(kpId)
        }
        cleanEvents.forEach {
            try {
                val responseClean = server.sendClean(
                    it.token.authHeader,
                    it.clean.kpId,
                    RequestClean(it.clean)
                ).execute()
                Timber.d("responseClean ${responseClean.code()}")
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
        val photoEvents = if (kpId < 0) {
            db.photoDao().getSendEvents()
        } else {
            db.photoDao().getSendKpIdEvents(kpId)
        }
        photoEvents.forEach {
            val photo = fileManager.readFile(it.photo.path) ?: return@forEach
            try {
                val responsePhoto = server.sendPhoto(
                    it.token.authHeader,
                    it.photo.kpId?.toString()?.toRequestBody(plaintText),
                    it.photo.type.toString().toRequestBody(plaintText),
                    it.photo.whenTime.toString(PATTERN_DATETIME).toRequestBody(plaintText),
                    it.photo.latitude.toString().toRequestBody(plaintText),
                    it.photo.longitude.toString().toRequestBody(plaintText),
                    photo
                ).execute()
                Timber.d("responsePhoto ${responsePhoto.code()}")
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
        if (all) {
            try {
                server.logout(preferences.authHeader).execute()
            } catch (e: Throwable) {
                Timber.e(e)
                return@coroutineScope Result.failure(
                    Data.Builder()
                        .putString(
                            EXTRA_WORKER_MESSAGE, if (BuildConfig.DEBUG) {
                                e.toString()
                            } else {
                                e.localizedMessage
                            }
                        )
                        .build()
                )
            }
        }
        Result.success()
    }

    companion object {

        const val NAME = "SEND"

        private const val PARAM_KP_ID = "kp_id"

        fun launch(context: Context, id: Int = -1): LiveData<WorkInfo?> {
            val request = OneTimeWorkRequestBuilder<SendWorker>()
                .setInputData(
                    Data.Builder()
                        .putInt(PARAM_KP_ID, id)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(NAME, ExistingWorkPolicy.KEEP, request)
                return getWorkInfoByIdLiveData(request.id)
            }
        }
    }
}