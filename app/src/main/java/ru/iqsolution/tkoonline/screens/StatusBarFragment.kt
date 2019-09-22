package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.status_bar.*
import org.jetbrains.anko.connectivityManager
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.SIMPLE_TIME
import ru.iqsolution.tkoonline.data.local.Preferences
import timber.log.Timber

class StatusBarFragment : BaseFragment() {

    private lateinit var preferences: Preferences

    @Volatile
    private var swapIcon = R.drawable.ic_swap_vert

    private val swapRunnable = Runnable {
        status_connection.setImageResource(swapIcon)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Preferences(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.status_bar, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        status_number.text = preferences.vehicleNumber ?: ""
        updateTime()
        status_location.setImageResource(R.drawable.ic_gps_fixed)
        status_connection.setImageResource(R.drawable.ic_swap_vert)
        status_uploads.setImageResource(R.drawable.ic_cloud_upload)
        activity?.apply {
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
            registerReceiver(receiver, IntentFilter().apply {
                // time
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
                // battery
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_BATTERY_LOW)
                addAction(Intent.ACTION_BATTERY_OKAY)
            })
        }
    }

    private fun updateTime() {
        status_time.text = SIMPLE_TIME.format(System.currentTimeMillis() - preferences.timeDifference)
    }

    @SuppressLint("SetTextI18n")
    private fun updateBattery(status: Int, level: Int) {
        when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> {
                status_battery.apply {
                    setImageResource(R.drawable.ic_battery_charging)
                    tag = R.drawable.ic_battery_charging
                }
                status_percent.text = ""
                return
            }
            BatteryManager.BATTERY_STATUS_FULL, BatteryManager.BATTERY_STATUS_DISCHARGING, BatteryManager.BATTERY_STATUS_NOT_CHARGING -> {
                status_battery.apply {
                    setImageResource(R.drawable.ic_battery_full)
                    tag = R.drawable.ic_battery_full
                }
            }
            // unknown
            else -> {
                if (status_battery.tag == R.drawable.ic_battery_charging) {
                    return
                }
                status_battery.apply {
                    setImageResource(R.drawable.ic_battery_full)
                    tag = R.drawable.ic_battery_full
                }
            }
        }
        if (level >= 0) {
            status_percent.text = "$level%"
        }
    }

    override fun onDestroyView() {
        activity?.apply {
            connectivityManager.unregisterNetworkCallback(callback)
            unregisterReceiver(receiver)
        }
        super.onDestroyView()
    }

    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Timber.d("Network on available")
            swapIcon = R.drawable.ic_swap_vert_green
            activity?.runOnUiThread(swapRunnable)
        }

        override fun onLost(network: Network) {
            Timber.d("Network on lost")
            swapIcon = R.drawable.ic_swap_vert
            activity?.runOnUiThread(swapRunnable)
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Status bar action: ${intent.action}")
            when (intent.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                    updateTime()
                }
                Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW, Intent.ACTION_BATTERY_OKAY -> {
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    Timber.d("On battery changes: status $status, level $level")
                    updateBattery(status, level)
                }
            }
        }
    }
}