package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.os.SystemClock
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.dialog_input.*
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.PASSWORD_RETRY
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.setMaxLength
import ru.iqsolution.tkoonline.extensions.setOnlyNumbers
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.base.BaseDialogFragment

@Suppress("DEPRECATION")
class PasswordDialog : BaseDialogFragment() {

    private val preferences: Preferences by instance()

    private var attemptsCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val password = preferences.lockPassword
        var time = preferences.blockTime
        dialog_close.isVisible = true
        dialog_title.text = "Пароль"
        dialog_input.apply {
            setOnlyNumbers()
            setMaxLength(4)
            if (password != null) {
                inputType = inputType or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
        }
        if (SystemClock.elapsedRealtime() - time < PASSWORD_RETRY) {
            retryLater()
        }
        dialog_action.text = "Подтвердить"
        dialog_action.setOnClickListener {
            val input = dialog_input.text.toString()
            dialog_error.text = ""
            when {
                SystemClock.elapsedRealtime() - time < PASSWORD_RETRY -> retryLater()
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
                            time = SystemClock.elapsedRealtime()
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
        dialog_input.setText("")
        dialog_error.text = "Попробуйте позже"
    }

    private fun onPrompted(afterSetup: Boolean) {
        dialog_input.setText("")
        activityCallback<SettingsListener> {
            if (afterSetup) {
                enterKioskMode()
            } else {
                openSettingsDialog()
            }
        }
    }

    companion object {

        private const val MAX_ATTEMPTS = 3
    }
}