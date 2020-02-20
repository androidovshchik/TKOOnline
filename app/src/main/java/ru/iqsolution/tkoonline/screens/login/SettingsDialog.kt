package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.dialog_settings.*
import org.jetbrains.anko.find
import ru.iqsolution.tkoonline.BaseApp
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.setTextSelection
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.base.BaseDialogFragment

@Suppress("DEPRECATION")
class SettingsDialog : BaseDialogFragment() {

    private var mEnableLock = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_settings, container, false)
    }

    @Suppress("ConstantConditionIf")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preferences = Preferences(context).apply {
            dialog_main_server.setTextSelection(mainServerAddress)
            dialog_telemetry_server.setTextSelection(mainTelemetryAddress)
            if (!BuildConfig.PROD) {
                find<Switch>(R.id.dev_file_logs).isChecked = enableLogs
                find<Switch>(R.id.dev_build_route).isChecked = showRoute
            }
            setAsLocked(enableLock)
        }
        if (!BuildConfig.PROD) {
            find<Switch>(R.id.dev_file_logs).setOnCheckedChangeListener { _, isChecked ->
                preferences.enableLogs = isChecked
                (activity?.application as? BaseApp)?.saveLogs(isChecked)
            }
            find<Switch>(R.id.dev_build_route).setOnCheckedChangeListener { _, isChecked ->
                preferences.showRoute = isChecked
            }
            find<View>(R.id.dev_build_route).setOnClickListener {
                makeCallback<SettingsListener> {
                    exportDb()
                }
            }
        }
        dialog_unlock.setOnClickListener {
            setLocked(!mEnableLock, preferences)
        }
        dialog_save.setOnClickListener {
            val slashRegex = "/+$".toRegex()
            val serverAddress = dialog_main_server.text.toString().trim()
                .replace(slashRegex, "")
            val telemetryAddress = dialog_telemetry_server.text.toString().trim()
                .replace(slashRegex, "")
            dialog_main_server.setTextSelection(serverAddress)
            dialog_telemetry_server.setTextSelection(telemetryAddress)
            preferences.bulk {
                mainServerAddress = serverAddress
                mainTelemetryAddress = telemetryAddress
            }
            dismiss()
        }
        dialog_close.setOnClickListener {
            dismiss()
        }
    }

    fun setAsLocked(enable: Boolean) {
        mEnableLock = enable
        dialog_unlock.text = if (enable) "Разблокировать" else "Заблокировать"
    }

    private fun setLocked(enable: Boolean, preferences: Preferences) {
        preferences.lockPassword = null
        makeCallback<SettingsListener> {
            if (enable) {
                openPasswordDialog()
            } else {
                exitKioskMode()
            }
        }
    }
}