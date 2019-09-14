package ru.iqsolution.tkoonline

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import timber.log.Timber

@Suppress("unused")
class MainApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {

    }

    override val kodeinTrigger = KodeinTrigger()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        kodeinTrigger.trigger()
    }
}