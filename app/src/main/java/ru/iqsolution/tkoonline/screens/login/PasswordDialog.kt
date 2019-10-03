package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_password.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.setMaxLength
import ru.iqsolution.tkoonline.extensions.setOnlyNumbers
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.base.BaseDialogFragment

class PasswordDialog : BaseDialogFragment() {

    private var attemptsCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
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
        if (System.currentTimeMillis() - time < WAIT_TIME) {
            retryLater()
        }
        dialog_accept.setOnClickListener {
            val input = dialog_password.text.toString()
            dialog_error.text = ""
            when {
                System.currentTimeMillis() - time < WAIT_TIME -> retryLater()
                input.length == 4 -> when (password) {
                    null -> {
                        preferences.lockPassword = input
                        onPrompted(true)
                    }
                    input -> onPrompted(false)
                    else -> {
                        attemptsCount++
                        if (attemptsCount < MAX_ATTEMPTS) {
                            val count = MAX_ATTEMPTS - attemptsCount
                            dialog_error.text = resources.getQuantityString(R.plurals.attempts, count, count)
                        } else {
                            attemptsCount = 0
                            time = System.currentTimeMillis()
                            preferences.blockTime = time
                            retryLater()
                        }
                    }
                }
                else -> dialog_error.text = "Введите 4 цифры"
            }
        }
        dialog_close.setOnClickListener {
            dismiss()
        }
    }

    private fun retryLater() {
        dialog_password.setText("")
        dialog_error.text = "Попробуйте позже"
    }

    private fun onPrompted(setup: Boolean) {
        dialog_password.setText("")
        activity?.let {
            if (it is DialogListener && !it.isFinishing) {
                if (setup) {
                    it.enterKioskMode()
                } else {
                    it.openSettingsDialog()
                }
            }
        }
    }

    companion object {

        private const val MAX_ATTEMPTS = 3

        private const val WAIT_TIME = 5 * 60 * 1000L
    }
}