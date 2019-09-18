package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_password.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.extensions.setMaxLength
import ru.iqsolution.tkoonline.extensions.setOnlyNumbers
import ru.iqsolution.tkoonline.screens.BaseDialogFragment

class PasswordDialog : BaseDialogFragment() {

    private var attemptsCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preferences = Preferences(context)
        val password = preferences.lockPassword
        var time = preferences.blockTime
        dialog_password.apply {
            setOnlyNumbers()
            setMaxLength(4)
            if (password != null) {
                inputType = inputType or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
        }
        if (shouldBlock(time)) {
            disableInput()
        }
        dialog_accept.onClick {
            dialog_error.text = ""
            val input = dialog_password.text.toString()
            if (shouldBlock(time)) {
                disableInput()
            } else if (checkPassword(input)) {
                when (password) {
                    null -> {
                        preferences.lockPassword = input
                        onPrompted()
                    }
                    input -> onPrompted()
                    else -> {
                        attemptsCount++
                        if (attemptsCount < MAX_ATTEMPTS) {
                            val count = MAX_ATTEMPTS - attemptsCount
                            dialog_password.isEnabled = true
                            dialog_error.text = resources.getQuantityString(R.plurals.attempts, count, count)
                        } else {
                            attemptsCount = 0
                            time = System.currentTimeMillis()
                            preferences.blockTime = time
                            disableInput()
                        }
                    }
                }
            }
        }
        dialog_close.onClick {
            dismiss()
        }
    }

    private fun checkPassword(input: CharSequence): Boolean {
        if (input.length != 4) {
            dialog_error.text = "Введите 4 цифры"
            return false
        }
        return true
    }

    private fun shouldBlock(time: Long) = System.currentTimeMillis() - time < WAIT_TIME

    private fun disableInput() {
        dialog_password.apply {
            setText("")
            isEnabled = false
        }
        dialog_error.text = "Попробуйте позже"
    }

    private fun onPrompted() {
        dialog_password.setText("")
        activity?.let {
            if (it is LoginActivity) {
                it.onSuccessPrompt()
            }
        }
    }

    companion object {

        private const val MAX_ATTEMPTS = 3

        private const val WAIT_TIME = 5 * 60 * 1000L
    }
}