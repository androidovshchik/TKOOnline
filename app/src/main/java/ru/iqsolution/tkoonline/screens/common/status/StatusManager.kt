@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.common.status

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.BatteryManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.jetbrains.anko.connectivityManager
import org.joda.time.DateTimeZone
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.isConnected
import ru.iqsolution.tkoonline.models.SimpleLocation
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class StatusManager(context: Context, listener: StatusListener) {

    private val reference = WeakReference(listener)

    private val connectivity = context.connectivityManager

    @SuppressLint("MissingPermission")
    fun register(context: Context) = context.run {
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
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter().apply {
                addAction(ACTION_LOCATION)
                addAction(ACTION_ROUTE)
                addAction(ACTION_CLOUD)
            })
    }

    fun unregister(context: Context) = context.run {
        connectivityManager.unregisterNetworkCallback(callback)
        unregisterReceiver(receiver)
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(receiver)
    }

    /**
     * NOTICE background thread
     */
    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            reference.get()?.onNetworkChanged(connectivity.isConnected)
        }

        override fun onLost(network: Network) {
            reference.get()?.onNetworkChanged(connectivity.isConnected)
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                    if (intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
                        try {
                            val timeZone = TimeZone.getDefault()
                            DateTimeZone.setDefault(DateTimeZone.forTimeZone(timeZone))
                            Timber.d("Changed default timezone to ${timeZone.id}")
                        } catch (e: Throwable) {
                            Timber.e(e)
                        }
                    }
                    reference.get()?.onTimeChanged()
                }
                ACTION_LOCATION -> {
                    if (intent.hasExtra(EXTRA_SYNC_LOCATION)) {
                        Timber.d("Received ACTION_LOCATION EXTRA_SYNC_LOCATION")
                        val location =
                            intent.getSerializableExtra(EXTRA_SYNC_LOCATION) as SimpleLocation
                        reference.get()?.onLocationResult(location)
                    }
                    if (intent.hasExtra(EXTRA_SYNC_AVAILABILITY)) {
                        val available = intent.getBooleanExtra(EXTRA_SYNC_AVAILABILITY, false)
                        reference.get()?.onLocationAvailability(available)
                    }
                }
                ACTION_ROUTE -> {
                    Timber.d("Received ACTION_ROUTE")
                    reference.get()?.onLocationEvent()
                }
                ACTION_CLOUD -> {
                    Timber.d("Received ACTION_CLOUD")
                    reference.get()?.onCloudChanged()
                }
                Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW, Intent.ACTION_BATTERY_OKAY -> {
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    reference.get()?.onBatteryChanged(status, level)
                }
            }
        }
    }
}
