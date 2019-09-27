package ru.iqsolution.tkoonline.screens.status

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.BatteryManager
import org.jetbrains.anko.connectivityManager
import org.joda.time.DateTimeZone
import ru.iqsolution.tkoonline.R
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class StatusManager(listener: StatusListener) {

    private val reference = WeakReference(listener)

    @Volatile
    private var swapIcon = R.drawable.ic_swap_vert

    private val swapRunnable = Runnable {
        reference.get()?.updateConnection(swapIcon)
    }

    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Timber.d("Network on available")
            swapIcon = R.drawable.ic_swap_vert_green
            reference.get()?.getActivity()?.runOnUiThread(swapRunnable)
        }

        override fun onLost(network: Network) {
            Timber.d("Network on lost")
            swapIcon = R.drawable.ic_swap_vert
            reference.get()?.getActivity()?.runOnUiThread(swapRunnable)
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Status bar action: ${intent.action}")
            when (intent.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                    if (intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
                        try {
                            val timeZone = TimeZone.getDefault()
                            DateTimeZone.setDefault(DateTimeZone.forTimeZone(timeZone))
                            Timber.d("Changed default timezone to ${timeZone.id}")
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                    reference.get()?.updateTime()
                }
                Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW, Intent.ACTION_BATTERY_OKAY -> {
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    Timber.d("On battery changes: status $status, level $level")
                    reference.get()?.updateBattery(status, level)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun register() {
        reference.get()?.getActivity()?.apply {
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

    fun unregister() {
        reference.get()?.getActivity()?.apply {
            connectivityManager.unregisterNetworkCallback(callback)
            unregisterReceiver(receiver)
        }
    }
}
