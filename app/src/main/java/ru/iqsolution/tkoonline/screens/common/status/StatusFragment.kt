package ru.iqsolution.tkoonline.screens.common.status

import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.gms.location.LocationSettingsStates
import kotlinx.android.synthetic.main.include_status.*
import org.jetbrains.anko.locationManager
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.PATTERN_TIME
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.BaseFragment
import ru.iqsolution.tkoonline.screens.base.IBaseView
import ru.iqsolution.tkoonline.services.LocationListener
import timber.log.Timber

/**
 * NOTICE should have an id [R.id.status_fragment]
 */
@Suppress("DEPRECATION")
class StatusFragment : BaseFragment(), SyncListener {

    private lateinit var syncManager: SyncManager

    private lateinit var preferences: Preferences

    private var serverTime: DateTime? = null

    @Volatile
    private var connectionIcon = R.drawable.ic_swap_vert

    private val connectionRunnable = Runnable {
        if (view != null) {
            status_connection.setImageResource(connectionIcon)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        syncManager = SyncManager(context, this)
        preferences = Preferences(context)
        try {
            serverTime = DateTime.parse(preferences.serverTime, PATTERN_DATETIME_ZONE)
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.include_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        status_number.text = preferences.vehicleNumber ?: ""
        onNetworkChanged(false)
        onCloudChanged(true, 0)
    }

    override fun onStart() {
        super.onStart()
        onTimeChanged()
        onLocationChanged(context.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        syncManager.register(context)
    }

    /**
     * Updates every minute
     */
    override fun onTimeChanged() {
        if (view == null) {
            return
        }
        serverTime?.let {
            /*val zone = DateTimeZone.forTimeZone(TimeZone.getDefault())
            status_time.text = it.plus(SystemClock.elapsedRealtime() - preferences.elapsedTime)
                .withZone(zone)
                .toString(PATTERN_TIME)*/
            status_time.text = DateTime.now().toString(PATTERN_TIME)
        }
    }

    private fun onLocationChanged(available: Boolean) {
        if (view == null) {
            return
        }
        status_location.setImageResource(if (available) R.drawable.ic_gps_fixed_green else R.drawable.ic_gps_fixed)
    }

    override fun onNetworkChanged(available: Boolean) {
        makeCallback<Any> {
            connectionIcon = if (available) R.drawable.ic_swap_vert_green else R.drawable.ic_swap_vert
            if (Looper.myLooper() == Looper.getMainLooper()) {
                connectionRunnable.run()
            } else {
                activity?.runOnUiThread(connectionRunnable)
            }
        }
    }

    override fun onCloudChanged() {
        makeCallback<IBaseView> {
            updateCloud()
        }
    }

    override fun onCloudChanged(hasData: Boolean, photoCount: Int) {
        if (view == null) {
            return
        }
        status_uploads.setImageResource(
            if (hasData) {
                R.drawable.ic_cloud_upload
            } else {
                R.drawable.ic_cloud_upload_green
            }
        )
        if (photoCount > 0) {
            status_count.apply {
                text = if (photoCount < 10) {
                    photoCount.toString()
                } else "*"
                isVisible = true
            }
        } else {
            status_count.apply {
                text = ""
                isVisible = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBatteryChanged(status: Int, level: Int) {
        if (view == null) {
            return
        }
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

    override fun onLocationEvent() {
        makeCallback<IBaseView> {
            updateRoute()
        }
    }

    /**
     * Will be called only from [ru.iqsolution.tkoonline.screens.base.BaseActivity]
     */
    override fun onLocationState(state: LocationSettingsStates?) {
        onLocationChanged(state?.isGpsUsable == true)
    }

    override fun onLocationResult(location: SimpleLocation) {
        makeCallback<LocationListener> {
            onLocationResult(location)
        }
    }

    override fun onLocationAvailability(available: Boolean) {
        onLocationChanged(available)
        makeCallback<LocationListener> {
            onLocationAvailability(available)
        }
    }

    override fun onStop() {
        syncManager.unregister(context)
        super.onStop()
    }
}