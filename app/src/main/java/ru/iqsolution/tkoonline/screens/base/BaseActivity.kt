package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStates
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.locationManager
import org.jetbrains.anko.toast
import org.kodein.di.Kodein
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.common.status.StatusFragment
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.presenterModule
import ru.iqsolution.tkoonline.screens.screenModule
import ru.iqsolution.tkoonline.telemetry.TelemetryRunnable
import ru.iqsolution.tkoonline.telemetry.TelemetryService
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseActivity<P : IBasePresenter<*>> : Activity(), IBaseView {

    private val parentKodein by closestKodein()

    override val kodein: Kodein by Kodein.lazy {

        extend(parentKodein)

        import(presenterModule)

        import(screenModule)
    }

    protected abstract val presenter: P

    protected val preferences: Preferences by instance()

    private var statusBar: StatusFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    @Suppress("DEPRECATION")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (this !is LoginActivity) {
            statusBar = fragmentManager.findFragmentById(R.id.status_fragment) as StatusFragment?
            if (!activityManager.isRunning<TelemetryService>()) {
                TelemetryRunnable(applicationContext).run()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (this !is LoginActivity) {
            updateCloud()
        }
    }

    override val isTouchable: Boolean
        get() = window.attributes.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE == 0

    override fun setTouchable(enable: Boolean) {
        if (enable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    @Suppress("ConstantConditionIf")
    override fun updateRoute() {
        if (!BuildConfig.PROD) {
            presenter.loadRoute()
        }
    }

    override fun onRoute(locationEvents: List<LocationEvent>) {}

    override fun updateCloud() {
        presenter.calculateSend()
    }

    override fun updateCloud(allCount: Int, photoCount: Int) {
        statusBar?.onCloudChanged(allCount > 0, photoCount)
    }

    override fun onUnhandledError(e: Throwable?) {}

    @Suppress("ConstantConditionIf")
    override fun showError(e: Throwable?) {
        showError(
            if (BuildConfig.PROD) {
                e?.localizedMessage ?: e.toString()
            } else {
                e.toString()
            }
        )
    }

    override fun showError(message: CharSequence?) {
        if (message != null) {
            toast(message)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            if (this !is LoginActivity) {
                checkLocation()
            }
        }
    }

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

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (resultCode == RESULT_OK) {
                    onLocationState(LocationSettingsStates.fromIntent(data))
                } else {
                    checkLocation()
                }
            }
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
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
    }
}