package ru.iqsolution.tkoonline.screens.status

import android.annotation.SuppressLint
import android.os.BatteryManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.status_bar.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.iqsolution.tkoonline.FORMAT_TIME
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.base.BaseFragment
import java.util.*

/**
 * Should have id [R.id.status_bar_fragment]
 */
class StatusFragment : BaseFragment(), SyncListener {

    private val syncManager = SyncManager(this)

    private lateinit var preferences: Preferences

    private lateinit var serverTime: DateTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Preferences(context)
        serverTime = DateTime.parse(preferences.serverTime, PATTERN_DATETIME)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.status_bar, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        status_number.text = preferences.vehicleNumber ?: ""
        updateTime()
        updateLocation(false)
        updateConnection(R.drawable.ic_swap_vert)
        status_uploads.setImageResource(R.drawable.ic_cloud_upload)
        syncManager.register()
    }

    override val baseActivity: BaseActivity<*>?
        get() = (activity as BaseActivity<*>?)?.also {
            if (it.isFinishing) {
                return null
            }
        }

    /**
     * Updates every minute
     */
    override fun updateTime() {
        if (view == null) {
            return
        }
        status_time.text = serverTime.plus(SystemClock.elapsedRealtime() - preferences.elapsedTime)
            .withZone(DateTimeZone.forTimeZone(TimeZone.getDefault()))
            .toString(FORMAT_TIME)
    }

    override fun updateLocation(available: Boolean) {
        if (view == null) {
            return
        }
        status_location.setImageResource(if (available) R.drawable.ic_gps_fixed_green else R.drawable.ic_gps_fixed)
    }

    override fun updateConnection(icon: Int) {
        if (view == null) {
            return
        }
        status_connection.setImageResource(icon)
    }

    @SuppressLint("SetTextI18n")
    override fun updateBattery(status: Int, level: Int) {
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

    override fun onDestroyView() {
        syncManager.unregister()
        super.onDestroyView()
    }
}