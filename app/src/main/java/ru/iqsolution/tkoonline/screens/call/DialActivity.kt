package ru.iqsolution.tkoonline.screens.call

import android.os.Bundle
import android.telecom.Call
import android.view.WindowManager
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_dial.*
import org.kodein.di.instance
import ru.iqsolution.tkoonline.EXTRA_CONTACT
import ru.iqsolution.tkoonline.EXTRA_DIRECTION
import ru.iqsolution.tkoonline.PHONE_MASK
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.ifNullOrBlank
import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import timber.log.Timber

class DialActivity : BaseActivity<DialContract.Presenter>(), DialContract.View {

    override val presenter: DialPresenter by instance()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        setContentView(R.layout.activity_dial)
        when (intent.getIntExtra(EXTRA_DIRECTION, Call.Details.DIRECTION_UNKNOWN)) {
            Call.Details.DIRECTION_INCOMING -> {
                tv_number.text = "Звонит:"
            }
            Call.Details.DIRECTION_OUTGOING -> {
                tv_number.text = "Звоним:"
            }
        }
        btn_answer.setOnClickListener {
            CallService.answer()
        }
        btn_hangup.setOnClickListener {
            CallService.hangup()
        }
        presenter.observeCalls()
        val phone = intent.data?.schemeSpecificPart
        val contact = intent.getStringExtra(EXTRA_CONTACT)
        if (!contact.isNullOrBlank()) {
            tv_name.text = contact
        } else {
            presenter.readContact(phone)
        }
        val mask = MaskImpl.createTerminated(UnderscoreDigitSlotsParser().parseSlots(PHONE_MASK))
        mask.insertFront(phone)
        tv_number.text = mask.toString()
    }

    override fun onContactInfo(contact: Contact?) {
        tv_name.text = contact?.name.ifNullOrBlank { "Неизвестный" }
    }

    override fun onCallState(state: Int) {
        tv_state.text = getStateDesc(state)
        btn_answer.isVisible = state == Call.STATE_RINGING
        btn_hangup.isEnabled = state in Call.STATE_DIALING..Call.STATE_ACTIVE
    }

    private fun getStateDesc(state: Int): String {
        return when (state) {
            Call.STATE_NEW -> "Инициализация..."
            Call.STATE_RINGING -> "Входящий вызов..."
            Call.STATE_DIALING -> "Исходящий вызов..."
            Call.STATE_ACTIVE -> "Идет разговор..."
            Call.STATE_HOLDING -> "Удержание..."
            Call.STATE_DISCONNECTED -> "Нет соединения..."
            Call.STATE_CONNECTING -> "Соединение..."
            Call.STATE_DISCONNECTING -> "Отсоединение..."
            Call.STATE_SELECT_PHONE_ACCOUNT -> "Нет разрешения..."
            Call.STATE_SIMULATED_RINGING -> "SIMULATED_RINGING"
            Call.STATE_AUDIO_PROCESSING -> "AUDIO_PROCESSING"
            else -> {
                Timber.w("Unknown state=$state")
                "Неизвестно..."
            }
        }
    }
}