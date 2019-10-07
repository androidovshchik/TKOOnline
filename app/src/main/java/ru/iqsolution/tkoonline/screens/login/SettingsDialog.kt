package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.dialog_login.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.base.BaseDialogFragment

class SettingsDialog : BaseDialogFragment() {

    private var mEnableLock = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preferences = Preferences(context).apply {
            dialog_main_server.setText(mainServerAddress)
            dialog_telemetry_server.setText(mainTelemetryAddress)
            setAsLocked(enableLock)
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
            dialog_main_server.setText(serverAddress)
            dialog_telemetry_server.setText(telemetryAddress)
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