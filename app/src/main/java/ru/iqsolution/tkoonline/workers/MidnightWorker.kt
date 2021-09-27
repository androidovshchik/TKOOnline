package ru.iqsolution.tkoonline.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.chibatching.kotpref.blockingBulk
import org.kodein.di.instance
import ru.iqsolution.tkoonline.exitUnexpected
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.Server
import timber.log.Timber
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class MidnightWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {

    private val preferences: Preferences by instance()

    private val server: Server by instance()

    override fun doWork(): Result {
        val header = preferences.authHeader ?: return Result.success()
        if (!applicationContext.exitUnexpected()) {
            try {
                server.logout(header).execute()
                preferences.blockingBulk {
                    logout()
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
        return Result.success()
    }

    companion object {

        private const val NAME = "MIDNIGHT"

        fun launch(context: Context) {
            // Также авторизация сбрасывается сервером ежедневно в 02:00 (UTC+3)
            val now = ZonedDateTime.now(ZoneOffset.ofHours(1))
            val delay = Duration.between(now, now.plusDays(1).truncatedTo(ChronoUnit.DAYS)).toMillis()
            val request = OneTimeWorkRequestBuilder<MidnightWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(NAME, ExistingWorkPolicy.REPLACE, request)
            }
        }
    }
}