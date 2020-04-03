package ru.iqsolution.tkoonline.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.local.entities.CleanEventToken
import ru.iqsolution.tkoonline.local.entities.LocationEventToken
import timber.log.Timber
import java.util.concurrent.TimeUnit

val remoteModule = Kodein.Module("remote") {

    bind<Gson>() with factory { pretty: Boolean ->
        GsonBuilder()
            .setLenient()
            .setExclusionStrategies(SerializedNameStrategy())
            .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
            .registerTypeAdapter(LocationEventToken::class.java, LocationEventTokenSerializer())
            .registerTypeAdapter(CleanEventToken::class.java, CleanEventTokenSerializer())
            .apply {
                if (pretty) {
                    setPrettyPrinting()
                }
            }
            .create()
    }

    @Suppress("ConstantConditionIf")
    bind<OkHttpClient>() with singleton {
        OkHttpClient.Builder().apply {
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            addInterceptor(AppInterceptor(instance()))
            addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                Timber.tag("NETWORK")
                    .d(message)
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            if (!BuildConfig.PROD) {
                addNetworkInterceptor(
                    Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor")
                        .newInstance() as Interceptor
                )
            }
        }.build()
    }

    bind<Server>() with singleton {
        Retrofit.Builder()
            .client(instance())
            .baseUrl("https://localhost/mobile/")// "localhost" will be replaced
            .addConverterFactory(GsonConverterFactory.create(instance(arg = false)))
            .build()
            .create(Server::class.java)
    }
}