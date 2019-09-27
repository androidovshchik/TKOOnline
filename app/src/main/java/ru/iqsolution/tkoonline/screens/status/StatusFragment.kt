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
import ru.iqsolution.tkoonline.screens.base.BaseFragment
import java.util.*

class StatusFragment : BaseFragment(), StatusListener {

    private val statusManager = StatusManager(this)

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
        status_location.setImageResource(R.drawable.ic_gps_fixed)
        updateConnection(R.drawable.ic_swap_vert)
        status_uploads.setImageResource(R.drawable.ic_cloud_upload)
        statusManager.init()
    }

    override fun updateTime() {
        status_time.text = serverTime.plus(SystemClock.elapsedRealtime() - preferences.elapsedTime)
            .withZone(DateTimeZone.forTimeZone(TimeZone.getDefault()))
            .toString(FORMAT_TIME)
    }

    override fun updateConnection(icon: Int) {
        status_connection.setImageResource(icon)
    }

    @SuppressLint("SetTextI18n")
    override fun updateBattery(status: Int, level: Int) {
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
        statusManager.release()
        super.onDestroyView()
    }
}