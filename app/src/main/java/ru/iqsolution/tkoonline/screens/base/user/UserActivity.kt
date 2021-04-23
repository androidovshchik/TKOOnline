package ru.iqsolution.tkoonline.screens.base.user

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStates
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.locationManager
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.common.status.StatusFragment
import ru.iqsolution.tkoonline.telemetry.TelemetryRunnable
import ru.iqsolution.tkoonline.telemetry.TelemetryService
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class UserActivity<P : IUserPresenter<*>> : BaseActivity<P>(), IUserView {

    private var statusBar: StatusFragment? = null

    @Suppress("DEPRECATION")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        statusBar = fragmentManager.findFragmentById(R.id.status_fragment) as? StatusFragment
        if (!activityManager.isRunning<TelemetryService>()) {
            TelemetryRunnable(applicationContext).run()
        }
    }

    override fun onStart() {
        super.onStart()
        updateCloud()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            checkLocation()
        }
    }

    @Suppress("ConstantConditionIf")
    override fun updateRoute() {
        if (!BuildConfig.PROD) {
            presenter.loadRoute()
        }
    }

    override fun updateCloud() {
        presenter.calculateSend()
    }

    override fun onCloudUpdate(allCount: Int, photoCount: Int) {
        statusBar?.onCloudChanged(allCount > 0, photoCount)
    }

    override fun onLocationState(state: LocationSettingsStates?) {
        statusBar?.onLocationState(state)
    }

    /**
     * Will be called from [StatusFragment]
     */
    override fun onLocationAvailability(available: Boolean) {}

    /**
     * Will be called from [StatusFragment]
     */
    override fun onLocationResult(location: SimpleLocation) {}

    private fun checkLocation() {
        val isGpsAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        onLocationState(LocationSettingsStates(isGpsAvailable, false, false, false, false, false))
        if (!isGpsAvailable) {
            LocationServices.getSettingsClient(this)
                .checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    onLocationState(it.locationSettingsStates)
                }
                .addOnFailureListener {
                    onLocationState(null)
                    if (it is ResolvableApiException) {
                        try {
                            it.startResolutionForResult(this, REQUEST_LOCATION)
                        } catch (e: Throwable) {
                            Timber.e(e)
                        }
                    } else {
                        Timber.e(it)
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (resultCode == RESULT_OK) {
                    onLocationState(LocationSettingsStates.fromIntent(data ?: return))
                } else {
                    checkLocation()
                }
            }
        }
    }

    companion object {

        private const val REQUEST_LOCATION = 100

        private val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            })
            /**
             * Whether or not location is required by the calling app in order to continue.
             * Set this to true if location is required to continue and false if having location provides better results,
             * but is not required. This changes the wording/appearance of the dialog accordingly.
             */
            .setAlwaysShow(true)
            .build()

        fun isAssignableFrom(className: String?): Boolean {
            return !className.isNullOrBlank() &&
                UserActivity::class.java.isAssignableFrom(Class.forName(className))
        }
    }
}