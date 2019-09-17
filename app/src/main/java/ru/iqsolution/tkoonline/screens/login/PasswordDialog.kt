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
import kotlin.math.absoluteValue

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
        var blockTime = preferences.nextAttemptsAfter.absoluteValue
        dialog_password.apply {
            setOnlyNumbers()
            setMaxLength(4)
            if (System.currentTimeMillis() - blockTime < WAIT_TIME) {
                error = "Попробуйте еще раз позже"
            }
        }
        dialog_accept.onClick {
            dialog_password.error = null
            val input = dialog_password.text.toString()
            if (input.length == 4) {
                when (password) {
                    null -> {
                        preferences.lockPassword = AESCBCPKCS7.encrypt(input)
                        dismissPrompted()
                    }
                    input -> dismissPrompted()
                    else -> {
                        dialog_password.error = if (System.currentTimeMillis() - blockTime >= WAIT_TIME) {
                            attemptsCount++
                            if (attemptsCount > MAX_ATTEMPTS) {
                                attemptsCount = 0
                                blockTime = System.currentTimeMillis()
                                preferences.nextAttemptsAfter = blockTime
                                "Попробуйте еще раз позже"
                            } else {
                                val count = MAX_ATTEMPTS - attemptsCount
                                resources.getQuantityString(R.plurals.attempts, count, count)
                            }
                        } else {
                            "Попробуйте еще раз позже"
                        }
                    }
                }
            } else {
                dialog_password.error = "Короткий пароль"
            }
        }
        dialog_close.onClick {
            dismiss()
        }
    }

    private fun dismissPrompted() {
        activity?.let {
            if (it is LoginActivity) {
                it.hasPromptedPassword = true
            }
        }
        dismiss()
    }

    companion object {

        private const val MAX_ATTEMPTS = 3

        private const val WAIT_TIME = 5 * 60 * 1000L
    }
}