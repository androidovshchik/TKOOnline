package ru.iqsolution.tkoonline.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.iqsolution.tkoonline.R
import timber.log.Timber

class StatusBarFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.status_bar, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        activity?.registerReceiver(batteryReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        })
    }

    override fun onDestroyView() {
        activity?.unregisterReceiver(batteryReceiver)
        super.onDestroyView()
    }

    private val batteryReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("** onReceive")
            if (intent.hasExtra(BatteryManager.EXTRA_LEVEL)) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                Timber.d("** level $level")
            }
            if (intent.hasExtra(BatteryManager.EXTRA_STATUS)) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                Timber.d("** status $status")
                val isCharging =
                    status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                Timber.d("** isCharging $isCharging")
            }
        }
    }
}