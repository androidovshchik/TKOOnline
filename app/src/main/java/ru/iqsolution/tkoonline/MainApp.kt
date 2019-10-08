package ru.iqsolution.tkoonline

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import coil.Coil
import coil.ImageLoader
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import net.danlew.android.joda.ResourceZoneInfoProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.notificationManager
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
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.PopulateTask
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.*
import ru.iqsolution.tkoonline.services.workers.MidnightWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Suppress("unused")
class MainApp : Application(), KodeinAware {

    override val kodein by Kodein.lazy {

        bind<OkHttpClient>() with provider {
            OkHttpClient.Builder().apply {
                addInterceptor(DomainInterceptor(applicationContext))
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

        bind<Preferences>() with provider {
            Preferences(applicationContext)
        }

        bind<FileManager>() with provider {
            FileManager(applicationContext)
        }

        bind<Server>() with singleton {
            Retrofit.Builder()
                .client(instance())
                .baseUrl("https://localhost/mobile/v1/")// "localhost" will be replaced
                .addConverterFactory(GsonConverterFactory.create(instance()))
                .build()
                .create(Server::class.java)
        }

        bind<Database>() with singleton {
            Room.databaseBuilder(applicationContext, Database::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(sqliteDatabase: SupportSQLiteDatabase) {
                        // may be put initial data here etc.
                        PopulateTask().execute(db)
                    }
                })
                .build()
        }
    }

    val db: Database by instance()

    val preferences: Preferences by instance()

    override fun onCreate() {
        super.onCreate()
        instance = this
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
        //FileManager(applicationContext).deleteAllFiles()
        //preferences.clear()
        //deleteDatabase("app.db")
    }

    companion object {

        lateinit var instance: MainApp
            private set
    }
}