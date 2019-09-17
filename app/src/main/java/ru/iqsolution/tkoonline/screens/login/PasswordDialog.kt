@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_password.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.local.AESCBCPKCS7
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.extensions.setMaxLength
import ru.iqsolution.tkoonline.extensions.setOnlyNumbers

class PasswordDialog : DialogFragment() {

    private var attemptsCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preferences = Preferences(context)
        val password = preferences.lockPassword?.let {
            AESCBCPKCS7.decrypt(it)
        }
        dialog_password.apply {
            setOnlyNumbers()
            setMaxLength(4)
            if (preferences.nextAttemptsAfter) {
                dialog_password.error = ""
            }
        }
        dialog_accept.onClick {
            val input = dialog_password.text.toString()
            if (input.length == 4) {
                when (password) {
                    null -> {
                        preferences.lockPassword = AESCBCPKCS7.encrypt(input)
                        dismiss()
                    }
                    input -> {

                    }
                    else -> {
                        attemptsCount++
                        if (attemptsCount >= 4) {
                            preferences.nextAttemptsAfter =
                        } else {
                            dialog_password.error = "Неверный пароль"
                        }
                    }
                }
            } else {
                dialog_password.error = "Требуется 4-х значный цифровой пароль"
            }
        }
        dialog_close.onClick {
            dismiss()
        }
    }
}