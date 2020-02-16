package ru.iqsolution.tkoonline.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.iqsolution.tkoonline.BuildConfig
import timber.log.Timber

val remoteModule = Kodein.Module("remote") {

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
            addInterceptor(DomainInterceptor(instance()))
            addInterceptor(TagInterceptor())
            addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {

                override fun log(message: String) {
                    Timber.tag("NETWORK")
                        .d(message)
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            if (BuildConfig.DEBUG) {
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
            .baseUrl("https://localhost/mobile/v1/")// "localhost" will be replaced
            .addConverterFactory(GsonConverterFactory.create(instance()))
            .build()
            .create(Server::class.java)
    }
}