package ru.iqsolution.tkoonline

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
import org.acra.config.MailSenderConfigurationBuilder
import org.acra.data.StringFormat
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Preferences
import timber.log.Timber
import java.io.File

@Suppress("unused")
class MainApp : BaseApp() {

    private val preferences: Preferences by instance()

    @Suppress("SpellCheckingInspection", "ConstantConditionIf")
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
        if (!BuildConfig.DEBUG) {
            ACRA.init(this, CoreConfigurationBuilder(applicationContext)
                .setBuildConfigClass(BuildConfig::class.java)
                .setReportFormat(StringFormat.KEY_VALUE_LIST)
                .setEnabled(true).apply {
                    getPluginConfigurationBuilder(MailSenderConfigurationBuilder::class.java)
                        .setMailTo("vladkalyuzhnyu@gmail.com")
                        .setResSubject(R.string.crash_subject)
                        .setReportFileName("report.txt")
                        .setReportAsFile(true)
                        .setEnabled(true)
                    getPluginConfigurationBuilder(DialogConfigurationBuilder::class.java)
                        .setResTheme(android.R.style.Theme_Material_Light_Dialog)
                        .setResTitle(R.string.crash_title)
                        .setResText(R.string.crash_text)
                        .setResCommentPrompt(R.string.crash_comment)
                        .setResEmailPrompt(R.string.crash_email)
                        .setEnabled(true)
                })
        }
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