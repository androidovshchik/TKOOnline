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
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.PatternFlattener
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
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
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.*
import ru.iqsolution.tkoonline.services.workers.MidnightWorker
import timber.log.Timber
import java.io.File

@Suppress("unused")
class MainApp : Application(), KodeinAware {

    override val kodein by Kodein.lazy {

        bind<Preferences>() with provider {
            Preferences(applicationContext)
        }

        bind<FileManager>() with provider {
            FileManager(applicationContext)
        }

        bind<Gson>() with provider {
            GsonBuilder()
                .setLenient()
                .setExclusionStrategies(SerializedNameStrategy())
                .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
                .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
                .create()
        }

        bind<OkHttpClient>() with singleton {
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
            }.build()
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
            Room.databaseBuilder(applicationContext, Database::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(sqliteDatabase: SupportSQLiteDatabase) {
                        // may be put initial data here etc.
                        // PopulateTask().execute(db)
                    }
                })
                .build()
        }
    }

    // val db: Database by instance()

    val preferences: Preferences by instance()

    override fun onCreate() {
        super.onCreate()
        instance = this
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
        /*FileManager(applicationContext).deleteAllFiles()
        //preferences.clear()
        deleteDatabase("app.db")*/
    }

    companion object {

        lateinit var instance: MainApp
            private set
    }
}