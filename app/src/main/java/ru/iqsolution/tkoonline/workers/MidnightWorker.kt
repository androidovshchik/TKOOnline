package ru.iqsolution.tkoonline.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.chibatching.kotpref.blockingBulk
import org.joda.time.DateTime
import org.joda.time.Duration
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.exitUnexpected
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MidnightWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val preferences: Preferences by instance()

    private val server: Server by instance()

    override fun doWork(): Result {
        val header = preferences.authHeader ?: return Result.success()
        preferences.blockingBulk {
            logout()
        }
        if (!applicationContext.exitUnexpected()) {
            try {
                server.logout(header).execute()
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
        return Result.success()
    }

    companion object {

        private const val NAME = "MIDNIGHT"

        fun launch(context: Context) {
            val now = DateTime.now()
            val delay = Duration(now, now.plusDays(1).withTime(2, 0, 0, 0)).millis
            val request = OneTimeWorkRequestBuilder<UpdateWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(NAME, ExistingWorkPolicy.REPLACE, request)
            }
        }
    }
}