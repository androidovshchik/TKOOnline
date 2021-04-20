package ru.iqsolution.tkoonline.screens.call

import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import ru.iqsolution.tkoonline.EXTRA_DIRECTION

class CallService : InCallService() {

    private val callback = object : Call.Callback() {

        override fun onStateChanged(call: Call, newState: Int) {
            state.tryEmit(newState)
        }
    }

    override fun onCallAdded(call: Call) {
        lastCall?.unregisterCallback(callback)
        state.tryEmit(call.state)
        call.registerCallback(callback)
        lastCall = call
        with(call.details) {
            startActivity(
                intentFor<DialActivity>(EXTRA_DIRECTION to callDirection)
                    .setData(handle).newTask()
            )
        }
    }

    override fun onCallRemoved(call: Call) {
        lastCall?.unregisterCallback(callback)
        lastCall = null
    }

    companion object {

        private var lastCall: Call? = null

        val state = MutableStateFlow(Call.STATE_NEW)

        fun answer() {
            lastCall?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        fun hangup() {
            lastCall?.disconnect()
        }
    }
}