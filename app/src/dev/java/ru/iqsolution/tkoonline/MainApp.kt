package ru.iqsolution.tkoonline

import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.PatternFlattener
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.facebook.stetho.Stetho
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Preferences
import timber.log.Timber
import java.io.File

@Suppress("unused")
class MainApp : BaseApp() {

    private val preferences: Preferences by instance()

    @Suppress("SpellCheckingInspection")
    override fun init() {
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
        Timber.plant(DevTree(preferences.enableLogs))
        Stetho.initialize(
            Stetho.newInitializerBuilder(applicationContext)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
                .build()
        )
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