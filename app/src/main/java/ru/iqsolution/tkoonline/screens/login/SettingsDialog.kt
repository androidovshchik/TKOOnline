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
import timber.log.Timber

class SettingsDialog : BaseDialogFragment() {

    private var isEnabledLock = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preferences = Preferences(context).apply {
            dialog_main_server.setText(mainServerAddress)
            dialog_telemetry_server.setText(mainTelemetryAddress)
            setLock(enableLock)
        }
        dialog_unlock.onClick {
            val enable = !isEnabledLock
            setLock(enable)
            // let's save also here
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
                enableLock = isEnabledLock
            }
            dismiss()
        }
        dialog_close.onClick {
            dismiss()
        }
    }

    private fun setLock(enable: Boolean) {
        isEnabledLock = enable
        Timber.d("isEnabledLock = $enable")
        dialog_unlock.text = if (enable) "Разблокировать" else "Заблокировать"
    }
}