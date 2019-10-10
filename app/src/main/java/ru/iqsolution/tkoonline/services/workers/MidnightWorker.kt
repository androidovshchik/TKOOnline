package ru.iqsolution.tkoonline.services.workers

import android.app.ActivityManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.chibatching.kotpref.blockingBulk
import kotlinx.coroutines.coroutineScope
import org.jetbrains.anko.*
import org.joda.time.DateTime
import org.joda.time.Duration
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.extensions.getActivities
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.LockActivity
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MidnightWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    val preferences: Preferences by instance()

    val server: Server by instance()

    override suspend fun doWork(): Result = coroutineScope {
        val header = preferences.authHeader
        if (header.endsWith("null", true)) {
            return@coroutineScope Result.success()
        }
        preferences.blockingBulk {
            logout()
        }
        applicationContext.apply {
            if (activityManager.getActivities(packageName) > 0) {
                startActivity(intentFor<LockActivity>().apply {
                    if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
                        clearTask()
                    } else {
                        clearTop()
                    }
                }.newTask())
            }
        }
        try {
            server.logout(header).execute()
        } catch (e: Throwable) {
            Timber.e(e)
        }
        Result.success()
    }

    companion object {

        private const val NAME = "MIDNIGHT"

        fun launch(context: Context) {
            val now = DateTime.now()
            val delay = Duration(now, now.plusDays(1).withTime(0, 0, 0, 0)).millis
            val request = PeriodicWorkRequestBuilder<MidnightWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.REPLACE, request)
            }
        }
    }
}