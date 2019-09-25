package ru.iqsolution.tkoonline

import android.app.*
import android.os.Bundle
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.facebook.stetho.Stetho
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import net.danlew.android.joda.ResourceZoneInfoProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.local.AppDatabase
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.PopulateTask
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.remote.*
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.services.AdminManager
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Suppress("unused")
class MainApp : Application(), KodeinAware {

    override val kodein by Kodein.lazy {

        bind<OkHttpClient>() with provider {
            OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {

                        override fun log(message: String) {
                            Timber.tag("NETWORK")
                                .d(message)
                        }
                    }).apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build()
        }

        bind<Gson>() with provider {
            GsonBuilder()
                .setLenient()
                .setExclusionStrategies(SerializedNameStrategy())
                .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
                .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
                .registerTypeAdapter(PlatformStatus::class.java, PlatformStatusSerializer())
                .registerTypeAdapter(PlatformStatus::class.java, PlatformStatusDeserializer())
                .create()
        }

        bind<Preferences>() with provider {
            Preferences(applicationContext)
        }

        bind<AdminManager>() with provider {
            AdminManager(applicationContext)
        }

        bind<FileManager>() with provider {
            FileManager(applicationContext)
        }

        bind<ServerApi>() with singleton {
            Retrofit.Builder()
                .client(instance())
                .baseUrl("https://example.com/")
                .addConverterFactory(GsonConverterFactory.create(instance()))
                .build()
                .create(ServerApi::class.java)
        }

        bind<AppDatabase>() with singleton {
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        PopulateTask().execute(appDb)
                    }
                })
                .build()
        }
    }

    val appDb: AppDatabase by instance()

    val preferences: Preferences by instance()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initialize(
                Stetho.newInitializerBuilder(applicationContext)
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
                    .build()
            )
        }
        DateTimeZone.setProvider(ResourceZoneInfoProvider(applicationContext))
        if (isOreoPlus()) {
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_DEFAULT, CHANNEL_DEFAULT, NotificationManager.IMPORTANCE_LOW).also {
                    it.lockscreenVisibility = Notification.VISIBILITY_SECRET
                }
            )
        }
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
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityStarted(activity: Activity) {
                if (activity !is LockActivity && activity !is LoginActivity) {
                    if (!preferences.isLoggedIn) {
                        startActivity(intentFor<LockActivity>().apply {
                            if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
                                clearTask()
                            } else {
                                clearTop()
                            }
                        }.newTask())
                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        })
    }
}