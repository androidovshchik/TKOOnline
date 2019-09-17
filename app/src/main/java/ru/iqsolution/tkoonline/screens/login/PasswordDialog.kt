@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_password.*
import org.jetbrains.anko.inputMethodManager
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
        }
        if (System.currentTimeMillis() - blockTime < WAIT_TIME) {
            dialog_error.text = "Попробуйте еще раз позже"
        }
        dialog_accept.onClick {
            dialog_error.text = ""
            val input = dialog_password.text.toString()
            if (input.length == 4) {
                when (password) {
                    null -> {
                        preferences.lockPassword = AESCBCPKCS7.encrypt(input)
                        onPrompted()
                    }
                    input -> onPrompted()
                    else -> {
                        dialog_error.text = if (System.currentTimeMillis() - blockTime >= WAIT_TIME) {
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
                dialog_error.text = "Короткий пароль"
            }
        }
        dialog_close.onClick {
            context.inputMethodManager.hideSoftInputFromWindow(getView()?.windowToken, 0)
            dismiss()
        }
    }

    override fun dismiss() {
        context.inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        super.dismiss()
    }

    private fun onPrompted() {
        activity?.let {
            if (it is LoginActivity) {
                it.onRemovePrompt(true)
            }
        }
    }

    companion object {

        private const val MAX_ATTEMPTS = 3

        private const val WAIT_TIME = 5 * 60 * 1000L
    }
}