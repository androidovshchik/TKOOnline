package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.dialog_login.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.base.BaseDialogFragment

class SettingsDialog : BaseDialogFragment() {

    private var isEnabledLock = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preferences = Preferences(context).apply {
            dialog_main_server.setText(mainServerAddress)
            dialog_telemetry_server.setText(mainTelemetryAddress)
            setLocked(enableLock, null)
        }
        dialog_unlock.onClick {
            setLocked(!isEnabledLock, preferences)
        }
        dialog_save.onClick {
            val slashRegex = "/+$".toRegex()
            val serverAddress = dialog_main_server.text.toString().trim()
                .replace(slashRegex, "")
            val telemetryAddress = dialog_telemetry_server.text.toString().trim()
                .replace(slashRegex, "")
            dialog_main_server.setText(serverAddress)
            dialog_telemetry_server.setText(telemetryAddress)
            preferences.bulk {
                mainServerAddress = serverAddress
                mainTelemetryAddress = telemetryAddress
            }
            dismiss()
        }
        dialog_close.onClick {
            dismiss()
        }
    }

    fun setLocked(enable: Boolean, preferences: Preferences?) {
        if (!(enable && preferences != null)) {
            isEnabledLock = enable
            dialog_unlock.text = if (enable) "Разблокировать" else "Заблокировать"
            if (preferences == null) {
                return
            }
            preferences.bulk {
                enableLock = enable
                lockPassword = null
            }
        }
        activity?.let {
            if (it is DialogCallback && !it.isFinishing) {
                if (enable) {
                    it.openPasswordDialog()
                } else {
                    it.exitKioskMode()
                }
            }
        }
    }
}