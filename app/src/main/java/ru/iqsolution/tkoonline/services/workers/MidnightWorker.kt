package ru.iqsolution.tkoonline.services.workers

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

class MidnightWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    override suspend fun doWork(): Result = coroutineScope {
        val now = DateTime.now()
        val timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
        val expiredTokens = arrayListOf<AccessToken>()
        db.tokenDao().apply {
            for (token in getTokens()) {
                if (token.expires.withZone(timeZone).isBefore(now)) {
                    expiredTokens.add(token)
                } else {
                    break
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
            val SELF_REMINDER_HOUR = 8

            if (DateTime.now().hourOfDay < SELF_REMINDER_HOUR) {
                delay = Duration(
                    DateTime.now(),
                    DateTime.now().withTimeAtStartOfDay().plusHours(SELF_REMINDER_HOUR)
                ).getStandardMinutes()
            } else {
                delay = Duration(
                    DateTime.now(),
                    DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(SELF_REMINDER_HOUR)
                ).getStandardMinutes()
            }


            val workRequest = PeriodicWorkRequest.Builder(
                WorkerReminderPeriodic::class.java!!,
                24,
                TimeUnit.HOURS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS
            )
                .setInitialDelay(delay.toLong(), TimeUnit.MINUTES)
                .addTag("send_reminder_periodic")
                .build()


            WorkManager.getInstance()
                .enqueueUniquePeriodicWork("send_reminder_periodic", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<MidnightWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MAX_BACKOFF_MILLIS + 1,
                    TimeUnit.MILLISECONDS
                )
                .setPeriodStartTime()
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniquePeriodicWork("MIDNIGHT", ExistingPeriodicWorkPolicy.KEEP, request)
            }
        }
    }
}