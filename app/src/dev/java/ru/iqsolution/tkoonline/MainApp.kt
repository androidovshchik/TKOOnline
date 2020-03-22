package ru.iqsolution.tkoonline

import android.webkit.WebView
import com.facebook.stetho.Stetho

@Suppress("unused")
class MainApp : BaseApp() {

    @Suppress("SpellCheckingInspection")
    override fun init() {
        super.init()
        Stetho.initialize(
            Stetho.newInitializerBuilder(applicationContext)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
                .build()
        )
        WebView.setWebContentsDebuggingEnabled(true)
    }

    @Suppress("RedundantOverride")
    override fun onCreate() {
        super.onCreate()
        //ru.iqsolution.tkoonline.local.FileManager(applicationContext).deleteAllFiles()
        //ru.iqsolution.tkoonline.local.Preferences(applicationContext).clear()
        //deleteDatabase("app.db")
    }
}