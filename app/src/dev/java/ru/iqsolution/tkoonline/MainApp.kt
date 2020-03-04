package ru.iqsolution.tkoonline

import android.webkit.WebView
import com.facebook.stetho.Stetho
import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.DialogConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.file.Directory

@Suppress("unused")
class MainApp : BaseApp() {

    @Suppress("SpellCheckingInspection", "ConstantConditionIf")
    override fun init(): Boolean {
        if (ACRA.isACRASenderServiceProcess()) {
            return false
        }
        super.init()
        Stetho.initialize(
            Stetho.newInitializerBuilder(applicationContext)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
                .build()
        )
        if (!BuildConfig.DEBUG) {
            ACRA.init(
                this, CoreConfigurationBuilder(applicationContext)
                    .setBuildConfigClass(BuildConfig::class.java)
                    .setReportFormat(StringFormat.KEY_VALUE_LIST)
                    .setApplicationLogFileDir(Directory.EXTERNAL_FILES)
                    .setEnabled(true).apply {
                        getPluginConfigurationBuilder(DialogConfigurationBuilder::class.java)
                            .setResTheme(android.R.style.Theme_Material_Light_Dialog)
                            .setTitle("Произошла ошибка")
                            .setText("Отчет об ошибке находится по пути ${fileManager.externalDir?.path}")
                            .setCommentPrompt("Пару слов о случившемся")
                            .setEnabled(true)
                    })
        }
        WebView.setWebContentsDebuggingEnabled(true)
        return true
    }

    @Suppress("RedundantOverride")
    override fun onCreate() {
        super.onCreate()
        //FileManager(applicationContext).deleteAllFiles()
        //preferences.clear()
        //deleteDatabase("app.db")
    }
}