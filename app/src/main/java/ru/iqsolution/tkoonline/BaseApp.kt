package ru.iqsolution.tkoonline

import android.app.*
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import coil.Coil
import coil.ImageLoader
import com.balsikandar.crashreporter.CrashReporter
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.PatternFlattener
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import net.danlew.android.joda.ResourceZoneInfoProvider
import okhttp3.OkHttpClient
import org.jetbrains.anko.*
import org.joda.time.DateTimeZone
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import ru.iqsolution.tkoonline.extensions.getTopActivity
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.extensions.longBgToast
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.localModule
import ru.iqsolution.tkoonline.remote.remoteModule
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.services.serviceModule
import ru.iqsolution.tkoonline.services.workers.SendWorker
import ru.iqsolution.tkoonline.services.workers.UpdateWorker
import timber.log.Timber

@Suppress("unused")
abstract class BaseApp : Application(), KodeinAware, CameraXConfig.Provider {

    override val kodein by Kodein.lazy {

        bind<Context>() with provider {
            applicationContext
        }

        import(localModule)

        import(remoteModule)

        import(serviceModule)
    }

    override fun getCameraXConfig() = Camera2Config.defaultConfig()

    private val preferences: Preferences by instance()

    private val fileManager: FileManager by instance()

    protected open fun init() {
        val config = LogConfiguration.Builder()
            .t()
            .build()
        val filePrinter = FilePrinter.Builder(fileManager.logsDir.path)
            .fileNameGenerator(DateFileNameGenerator())
            .backupStrategy(NeverBackupStrategy())
            .flattener(PatternFlattener("{d yyyy-MM-dd HH:mm:ss.SSS} {l}: {m}"))
            .build()
        XLog.init(config, filePrinter)
        Timber.plant(LogTree(preferences.enableLogs))
        CrashReporter.initialize(applicationContext, fileManager.reportsDir.path)
    }

    override fun onCreate() {
        super.onCreate()
        init()
        if (isOreoPlus()) {
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_DEFAULT, CHANNEL_DEFAULT, NotificationManager.IMPORTANCE_LOW).also {
                    it.lockscreenVisibility = Notification.VISIBILITY_SECRET
                }
            )
        }
        DateTimeZone.setProvider(ResourceZoneInfoProvider(applicationContext))
        Coil.setDefaultImageLoader(ImageLoader(applicationContext) {
            availableMemoryPercentage(0.5)
            bitmapPoolPercentage(0.5)
            okHttpClient {
                OkHttpClient.Builder()
                    .cache(null)
                    .build()
            }
        })
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath(null)
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }
}

@WorkerThread
fun Context.exitUnexpected(): Boolean {
    SendWorker.cancel(applicationContext)
    UpdateWorker.cancel(applicationContext)
    when (activityManager.getTopActivity(packageName)) {
        null -> return false
        LockActivity::class.java.name, LoginActivity::class.java.name -> {
        }
        else -> {
            startActivity(intentFor<LockActivity>(EXTRA_TROUBLE_EXIT to true).apply {
                if (activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                    clearTop()
                } else {
                    clearTask()
                }
                newTask()
            })
            longBgToast("Требуется повторно авторизоваться")
        }
    }
    return true
}