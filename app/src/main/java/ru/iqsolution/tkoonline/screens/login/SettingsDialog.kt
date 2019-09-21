package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.dialog_login.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.screens.BaseDialogFragment

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
            setLocked(enableLock)
        }
        dialog_unlock.onClick {
            val enable = !isEnabledLock
            setLocked(enable)
            preferences.enableLock = enable
            activity?.let {
                if (it is LoginActivity) {
                    it.onKioskMode(enable)
                }
            }
        }
        dialog_save.onClick {
            preferences.bulk {
                mainServerAddress = dialog_main_server.text.toString().trim()
                mainTelemetryAddress = dialog_telemetry_server.text.toString().trim()
            }
            dismiss()
        }
        dialog_close.onClick {
            dismiss()
        }
    }

    private fun setLocked(enable: Boolean) {
        isEnabledLock = enable
        dialog_unlock.text = if (enable) "Разблокировать" else "Заблокировать"
    }
}