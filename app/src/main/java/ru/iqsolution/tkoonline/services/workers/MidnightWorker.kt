package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import org.jetbrains.anko.activityManager
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.services.BaseWorker
import timber.log.Timber

class MidnightWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    val preferences: Preferences by instance()

    val server: Server by instance()

    override suspend fun doWork(): Result = coroutineScope {
        val header = preferences.authHeader
        if (header.endsWith("null")) {
            return@coroutineScope Result.success()
        }
        preferences.logout()
        applicationContext.activityManager.apply {
            // todo may be start login activity from here
        }
        try {
            server.logout(header).execute()
        } catch (e: Throwable) {
            Timber.e(e)
        }
        Result.success()
    }

    companion object {

        const val TAG = "MIDNIGHT"

        fun launch(context: Context) {
            val request = OneTimeWorkRequestBuilder<MidnightWorker>()
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(TAG, ExistingWorkPolicy.KEEP, request)
            }
        }
    }
}