package ru.iqsolution.tkoonline.screens.call

import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.telecom.Call.Details.DIRECTION_INCOMING
import android.telecom.Call.Details.DIRECTION_OUTGOING
import android.telecom.CallAudioState
import android.view.WindowManager
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_dial.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.jetbrains.anko.audioManager
import org.kodein.di.instance
import ru.iqsolution.tkoonline.ACTION_AUDIO
import ru.iqsolution.tkoonline.EXTRA_DIRECTION
import ru.iqsolution.tkoonline.EXTRA_ROUTE
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.ifNullOrBlank
import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import timber.log.Timber

class DialActivity : BaseActivity<DialContract.Presenter>(), DialContract.View {

    override val presenter: DialPresenter by instance()

    private var lastState = Call.STATE_NEW

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        setContentView(R.layout.activity_dial)
        toolbar_back.setOnClickListener {
            onBackPressed()
        }
        toolbar_title.text = "Завершить вызов"
        iv_speaker.setOnClickListener {
            toggleSpeaker(!audioManager.isSpeakerphoneOn)
        }
        toggleSpeaker(preferences.useSpeaker)
        if (intent.getIntExtra(EXTRA_DIRECTION, DIRECTION_OUTGOING) == DIRECTION_INCOMING) {
            tv_status.text = "Звонит:"
        } else {
            tv_status.text = "Звоним:"
        }
        btn_answer.setOnClickListener {
            CallService.answer()
        }
        btn_hangup.setOnClickListener {
            CallService.hangup()
        }
        presenter.observeCalls()
        val phone = intent.data?.schemeSpecificPart?.replace("[^+0-9*#]".toRegex(), "")
        tv_number.text = phone
        presenter.readContact(phone)
    }

    override fun onContactInfo(contact: Contact?) {
        tv_name.text = contact?.name.ifNullOrBlank { "Неизвестный" }
    }

    override fun onCallState(state: Int) {
        lastState = state
        tv_state.text = getStateDesc(state)
        btn_answer.isVisible = state == Call.STATE_RINGING
        btn_hangup.isEnabled = state in Call.STATE_DIALING..Call.STATE_ACTIVE
    }

    private fun toggleSpeaker(enable: Boolean) {
        val intent = Intent(ACTION_AUDIO)
        if (enable) {
            sendBroadcast(intent.putExtra(EXTRA_ROUTE, CallAudioState.ROUTE_SPEAKER))
            iv_speaker.setImageResource(R.drawable.ic_baseline_volume_up_24)
            preferences.useSpeaker = true
        } else {
            sendBroadcast(intent.putExtra(EXTRA_ROUTE, CallAudioState.ROUTE_WIRED_OR_EARPIECE))
            iv_speaker.setImageResource(R.drawable.ic_baseline_volume_off_24)
            preferences.useSpeaker = false
        }
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

    override fun onDestroy() {
        if (lastState != Call.STATE_DISCONNECTING && lastState != Call.STATE_DISCONNECTED) {
            CallService.hangup()
        }
        super.onDestroy()
    }
}