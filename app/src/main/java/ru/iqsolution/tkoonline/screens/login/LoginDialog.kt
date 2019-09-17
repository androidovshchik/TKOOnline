@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.dialog_login.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.local.Preferences

class LoginDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_login)
        val preferences = Preferences(context)
        dialog_main_server.setText(preferences.mainServerAddress)
        dialog_telemetry_server.setText(preferences.mainTelemetryAddress)
        dialog_unlock.onClick {

        }
        dialog_save.onClick {
            preferences.bulk {
                preferences.mainServerAddress = dialog_main_server.text.toString().trim()
                preferences.mainServerAddress = dialog_main_server.text.toString().trim()
            }
            ownerActivity?.let {
                if (it is LoginActivity) {
                    it.
                }
            }
        }
        dialog_close.onClick {
            hide()
        }
    }
}