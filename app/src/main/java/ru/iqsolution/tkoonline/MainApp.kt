package ru.iqsolution.tkoonline

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
import org.jetbrains.anko.notificationManager
import org.joda.time.DateTimeZone
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.localModule
import ru.iqsolution.tkoonline.remote.remoteModule
import ru.iqsolution.tkoonline.services.workers.MidnightWorker
import timber.log.Timber
import java.io.File

@Suppress("unused")
class MainApp : Application(), KodeinAware, CameraXConfig.Provider {

    override val kodein by Kodein.lazy {

        bind<Context>() with provider {
            applicationContext
        }

        import(localModule)

        import(remoteModule)
    }

    val preferences: Preferences by instance()

    override fun getCameraXConfig() = Camera2Config.defaultConfig()

    override fun onCreate() {
        super.onCreate()
        getExternalFilesDir(null)?.let {
            val folder = File(it, "logs").apply {
                mkdirs()
            }
            val config = LogConfiguration.Builder()
                .t()
                .build()
            val filePrinter = FilePrinter.Builder(folder.path)
                .fileNameGenerator(DateFileNameGenerator())
                .backupStrategy(NeverBackupStrategy())
                .flattener(PatternFlattener("{d yyyy-MM-dd HH:mm:ss.SSS} {l}: {m}"))
                .build()
            XLog.init(config, filePrinter)
        }
        Timber.plant(LogTree(preferences.enableLogs))
        if (BuildConfig.DEBUG) {
            Class.forName("com.facebook.stetho.Stetho")
                .getDeclaredMethod("initializeWithDefaults", Context::class.java)
                .invoke(null, applicationContext)
        }
        DateTimeZone.setProvider(ResourceZoneInfoProvider(applicationContext))
        if (isOreoPlus()) {
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_DEFAULT, CHANNEL_DEFAULT, NotificationManager.IMPORTANCE_LOW).also {
                    it.lockscreenVisibility = Notification.VISIBILITY_SECRET
                }
            )
        }
        MidnightWorker.launch(applicationContext)
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
        /*FileManager(applicationContext).deleteAllFiles()
        //preferences.clear()
        deleteDatabase("app.db")*/
    }
}