package ru.iqsolution.tkoonline

import android.webkit.WebView
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.PatternFlattener
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.facebook.stetho.Stetho
import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.DialogConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.file.Directory
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import timber.log.Timber

@Suppress("unused")
class MainApp : BaseApp() {

    private val preferences: Preferences by instance()

    private val fileManager: FileManager by instance()

    @Suppress("SpellCheckingInspection", "ConstantConditionIf")
    override fun init(): Boolean {
        if (ACRA.isACRASenderServiceProcess()) {
            return false
        }
        val config = LogConfiguration.Builder()
            .t()
            .build()
        val filePrinter = FilePrinter.Builder(fileManager.logsDir.path)
            .fileNameGenerator(DateFileNameGenerator())
            .backupStrategy(NeverBackupStrategy())
            .flattener(PatternFlattener("{d yyyy-MM-dd HH:mm:ss.SSS} {l}: {m}"))
            .build()
        XLog.init(config, filePrinter)
        Timber.plant(DevTree(preferences.enableLogs))
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

    override fun saveLogs(enable: Boolean) {
        DevTree.saveToFile = enable
    }
}