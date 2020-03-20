package ru.iqsolution.tkoonline.workers

import android.content.Context
import androidx.work.*
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AuthWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val preferences: Preferences by instance()

    private val server: Server by instance()

    override fun doWork(): Result {
        val header = preferences.authHeader ?: return Result.success()
        try {
            server.getPlatforms(header, preferences.serverDay).execute()
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return Result.success()
    }

    companion object {

        private const val NAME = "AUTH"

        fun launch(context: Context) {
            val request = PeriodicWorkRequestBuilder<AuthWorker>(
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS
            ).build()
            WorkManager.getInstance(context).apply {
                enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.REPLACE, request)
            }
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).apply {
                cancelUniqueWork(NAME)
            }
        }
    }
}