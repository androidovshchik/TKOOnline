package ru.iqsolution.tkoonline

import android.app.*
import android.content.Context
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import coil.Coil
import coil.ImageLoader
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
import org.kodein.di.generic.provider
import ru.iqsolution.tkoonline.extensions.getActivities
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.local.localModule
import ru.iqsolution.tkoonline.remote.remoteModule
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.services.serviceModule
import ru.iqsolution.tkoonline.services.workers.MidnightWorker
import ru.iqsolution.tkoonline.services.workers.SendWorker
import ru.iqsolution.tkoonline.services.workers.UpdateWorker

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

    protected open fun init() {}

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
        MidnightWorker.launch(applicationContext)
    }

    open fun saveLogs(enable: Boolean) {}
}

fun Context.exitUnexpected(): Boolean {
    SendWorker.cancel(applicationContext)
    UpdateWorker.cancel(applicationContext)
    if (activityManager.getActivities(packageName) > 0) {
        startActivity(intentFor<LockActivity>().apply {
            if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
                clearTask()
            } else {
                clearTop()
            }
            newTask()
        })
        return true
    }
    return false
}