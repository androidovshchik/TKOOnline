package ru.iqsolution.tkoonline

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.joda.time.DateTime
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.iqsolution.tkoonline.data.local.AppDatabase
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.data.remote.DateTimeDeserializer
import ru.iqsolution.tkoonline.data.remote.DateTimeSerializer
import ru.iqsolution.tkoonline.data.remote.SerializedNameStrategy
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Suppress("unused")
class MainApplication : Application(), KodeinAware {

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
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    addNetworkInterceptor(StethoInterceptor())
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
                .create()
        }

        bind<ServerApi>() with singleton {
            Retrofit.Builder()
                .client(instance())
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(instance()))
                .build()
                .create(ServerApi::class.java)
        }

        bind<AppDatabase>() with singleton {
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .build()
        }

        bind<Preferences>() with provider {
            Preferences(applicationContext)
        }
    }

    val preferences: Preferences by instance()

    override val kodeinTrigger = KodeinTrigger()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        kodeinTrigger.trigger()
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
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                Stetho.newInitializerBuilder(applicationContext)
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
                    .build()
            )
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityStarted(activity: Activity) {
                if (activity !is LoginActivity) {
                    if (!preferences.isLoggedIn) {
                        startActivity(
                            intentFor<LoginActivity>()
                                .clearTask()
                        )
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