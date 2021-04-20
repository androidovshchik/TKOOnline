package ru.iqsolution.tkoonline.screens.call

import android.os.Bundle
import android.telecom.Call
import android.view.WindowManager
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_dial.*
import org.kodein.di.instance
import ru.iqsolution.tkoonline.EXTRA_PHONE
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseActivity

class DialActivity : BaseActivity<DialContract.Presenter>(), DialContract.View {

    override val presenter: DialPresenter by instance()

    private var number: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        setContentView(R.layout.activity_dial)
        //setTurnScreenOn(true)
        btn_answer.setOnClickListener {
            CallService.answer()
        }
        btn_hangup.setOnClickListener {
            CallService.hangup()
        }
        tv_number.text = intent.getStringExtra(EXTRA_PHONE)
        presenter.observeCalls()
    }

    override fun onCallState(state: Int) {
        tv_state.text = "state $state"
        btn_answer.isVisible = state == Call.STATE_RINGING
        btn_hangup.isVisible =
            state in listOf(Call.STATE_DIALING, Call.STATE_RINGING, Call.STATE_ACTIVE)
    }
}