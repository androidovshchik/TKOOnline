package ru.iqsolution.tkoonline.screens.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telecom.Call
import android.telecom.Call.Details.DIRECTION_INCOMING
import android.telecom.Call.Details.DIRECTION_OUTGOING
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.telecom.VideoProfile
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import ru.iqsolution.tkoonline.ACTION_AUDIO
import ru.iqsolution.tkoonline.EXTRA_DIRECTION
import ru.iqsolution.tkoonline.EXTRA_ROUTE
import ru.iqsolution.tkoonline.extensions.isQPlus
import timber.log.Timber

class CallService : InCallService() {

    private val callback = object : Call.Callback() {

        override fun onStateChanged(call: Call, newState: Int) {
            state.tryEmit(newState)
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_AUDIO -> {
                    Timber.d("Received ACTION_AUDIO")
                    setAudioRoute(intent.getIntExtra(EXTRA_ROUTE, CallAudioState.ROUTE_EARPIECE))
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(receiver, IntentFilter(ACTION_AUDIO))
    }

    override fun onCallAdded(call: Call) {
        lastCall?.unregisterCallback(callback)
        state.tryEmit(call.state)
        call.registerCallback(callback)
        lastCall = call
        with(call.details) {
            val direction = if (!isQPlus()) {
                if (call.state == Call.STATE_RINGING) DIRECTION_INCOMING else DIRECTION_OUTGOING
            } else {
                callDirection
            }
            startActivity(
                intentFor<DialActivity>(EXTRA_DIRECTION to direction)
                    .setData(handle).newTask()
            )
        }
    }

    override fun onCallRemoved(call: Call) {
        lastCall?.unregisterCallback(callback)
        lastCall = null
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
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