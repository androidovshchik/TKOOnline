package ru.iqsolution.tkoonline

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import coil.Coil
import coil.ImageLoader
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
import org.kodein.di.*
import ru.iqsolution.tkoonline.extensions.getTopActivity
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.localModule
import ru.iqsolution.tkoonline.remote.remoteModule
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.base.user.UserActivity
import ru.iqsolution.tkoonline.screens.call.DialActivity
import ru.iqsolution.tkoonline.workers.SendWorker
import ru.iqsolution.tkoonline.workers.UpdateWorker
import timber.log.Timber

@Suppress("unused")
abstract class BaseApp : Application(), DIAware, CameraXConfig.Provider {

    override val di by DI.lazy {

        bind<Context>() with provider {
            applicationContext
        }

        import(managerModule)

        import(localModule)

        import(remoteModule)
    }

    override fun getCameraXConfig() = Camera2Config.defaultConfig()

    private val preferences: Preferences by instance()

    private val fileManager: FileManager by instance()

    protected open fun init() {
        val config = LogConfiguration.Builder()
            .enableThreadInfo()
            .build()
        val filePrinter = FilePrinter.Builder(fileManager.logsDir.path)
            .fileNameGenerator(DateFileNameGenerator())
            .backupStrategy(NeverBackupStrategy())
            .flattener(PatternFlattener("{d yyyy-MM-dd HH:mm:ss.SSS} {l}: {m}"))
            .build()
        XLog.init(config, filePrinter)
        Timber.plant(LogTree(preferences.enableLogs))
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            XLog.e(t.name, e)
        }
    }

    override fun onCreate() {
        super.onCreate()
        init()
        if (isOreoPlus()) {
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_DEFAULT, "Default channel", NotificationManager.IMPORTANCE_LOW).also {
                    it.lockscreenVisibility = Notification.VISIBILITY_SECRET
                }
            )
        }
        DateTimeZone.setProvider(ResourceZoneInfoProvider(applicationContext))
        Coil.setImageLoader(
            ImageLoader.Builder(applicationContext)
                .availableMemoryPercentage(0.5)
                .bitmapPoolPercentage(0.5)
                .okHttpClient(
                    OkHttpClient.Builder()
                        .cache(null)
                        .build()
                )
                .build()
        )
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
    activityManager.getTopActivity(packageName)?.let {
        if (UserActivity.isAssignableFrom(it) || it == DialActivity::class.java.name) {
            startActivity(intentFor<LockActivity>(EXTRA_TROUBLE_EXIT to true).clearTask().newTask())
        }
        return true
    }
    return false
}