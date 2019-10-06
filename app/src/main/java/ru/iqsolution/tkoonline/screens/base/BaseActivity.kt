package ru.iqsolution.tkoonline.screens.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.WindowManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStates
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.platforms.WaitDialog
import ru.iqsolution.tkoonline.screens.status.StatusFragment
import ru.iqsolution.tkoonline.services.LocationListener
import ru.iqsolution.tkoonline.services.TelemetryService
import timber.log.Timber

@SuppressLint("Registered")
@Suppress("MemberVisibilityCanBePrivate")
open class BaseActivity<T : BasePresenter<out IBaseView>> : Activity(), IBaseView, ServiceConnection, LocationListener {

    open val attachService = false

    protected lateinit var presenter: T

    protected lateinit var preferences: Preferences

    protected var telemetryService: TelemetryService? = null

    private var statusBar: StatusFragment? = null

    private var waitDialog: WaitDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        preferences = Preferences(applicationContext)
        if (this !is LoginActivity) {
            TelemetryService.start(applicationContext)
        }
    }

    @Suppress("DEPRECATION")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        statusBar = fragmentManager.findFragmentById(R.id.status_fragment) as StatusFragment?
    }

    override fun onStart() {
        super.onStart()
        if (attachService) {
            bindService(intentFor<TelemetryService>(), this, Context.BIND_AUTO_CREATE)
        }
    }

    override fun updateCloud(clean: Int, photo: Int) {
        statusBar?.onPhotoCountChanged()
    }

    /**
     * Should be called from [StatusFragment]
     */
    override fun checkLocation() {
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
                    } catch (e: IntentSender.SendIntentException) {
                        Timber.e(e)
                    }
                } else {
                    Timber.e(it)
                }
            }
    }

    @Suppress("DEPRECATION")
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

    override fun showLoading() {
        if (waitDialog == null) {
            waitDialog = WaitDialog(this)
        }
        waitDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    override fun hideLoading() {
        waitDialog?.hide()
    }

    override fun showError(message: CharSequence?) {
        message?.let {
            toast(it)
        }
    }

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        telemetryService = (binder as TelemetryService.Binder).service
    }

    override fun onServiceDisconnected(name: ComponentName) {
        telemetryService = null
    }

    override fun onStop() {
        if (telemetryService != null) {
            unbindService(this)
        }
        super.onStop()
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
        waitDialog?.dismiss()
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