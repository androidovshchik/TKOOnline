@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.dialog_login.*
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.local.Preferences

class SettingsDialog : DialogFragment() {

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
            setLock(!isEnabledLock)
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

    override fun dismiss() {
        context.inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        super.dismiss()
    }

    private fun setLock(enable: Boolean) {
        isEnabledLock = enable
        dialog_unlock.text = if (enable) "Разблокировать" else "Заблокировать"
    }
}