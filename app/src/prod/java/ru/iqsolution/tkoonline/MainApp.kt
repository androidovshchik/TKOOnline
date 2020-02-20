package ru.iqsolution.tkoonline

import android.content.Context
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.PatternFlattener
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Preferences
import timber.log.Timber
import java.io.File

@Suppress("unused")
class MainApp : BaseApp() {

    val preferences: Preferences by instance()

    override fun onCreate() {
        super.onCreate()
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
            Class.forName("com.facebook.stetho.Stetho")
                .getDeclaredMethod("initializeWithDefaults", Context::class.java)
                .invoke(null, applicationContext)
        }
        /*FileManager(applicationContext).deleteAllFiles()
        //preferences.clear()
        deleteDatabase("app.db")*/
    }
}