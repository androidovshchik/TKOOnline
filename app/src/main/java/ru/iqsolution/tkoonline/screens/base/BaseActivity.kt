package ru.iqsolution.tkoonline.screens.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.FileProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStates
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.screens.WaitDialog
import ru.iqsolution.tkoonline.screens.status.StatusFragment
import ru.iqsolution.tkoonline.services.LocationListener
import ru.iqsolution.tkoonline.services.LocationManager
import timber.log.Timber

@SuppressLint("Registered")
@Suppress("MemberVisibilityCanBePrivate")
open class BaseActivity<T : BasePresenter<*>> : Activity(), IBaseView, LocationListener {

    protected lateinit var presenter: T

    protected lateinit var fileManager: FileManager

    private var waitDialog: WaitDialog? = null

    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        fileManager = FileManager(applicationContext)
    }

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

    /**
     * Should be called from [ru.iqsolution.tkoonline.screens.status.StatusFragment]
     */
    fun checkLocation() {
        LocationServices.getSettingsClient(this)
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                    .addLocationRequest(LocationManager.locationRequest)
                    /**
                     * Whether or not location is required by the calling app in order to continue.
                     * Set this to true if location is required to continue and false if having location provides better results,
                     * but is not required. This changes the wording/appearance of the dialog accordingly.
                     */
                    .setAlwaysShow(true)
                    .build()
            )
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
        fragmentManager.findFragmentById(R.id.status_bar_fragment)?.let {
            if (it is StatusFragment) {
                it.onLocationState(state)
            }
        }
    }

    /**
     * Will be called from [ru.iqsolution.tkoonline.screens.status.StatusFragment]
     */
    override fun onLocationAvailability(available: Boolean) {}

    /**
     * Will be called from [ru.iqsolution.tkoonline.screens.status.StatusFragment]
     */
    override fun onLocationResult(location: Location) {}

    protected fun takePhoto() {
        if (photoPath != null) {
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val file = fileManager.createFile()
            photoPath = file.path
            val uri = FileProvider.getUriForFile(applicationContext, "$packageName.fileprovider", file)
            startActivityForResult(intent.apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }, REQUEST_PHOTO)
        } else {
            toast("Не найдено приложение для фото")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (resultCode == RESULT_OK) {
                    // All required changes were successfully made
                    onLocationState(LocationSettingsStates.fromIntent(data))
                } else {
                    // The user was asked to change settings, but chose not to
                    checkLocation()
                }
            }
            REQUEST_PHOTO -> {
                photoPath?.also {
                    if (resultCode == RESULT_OK) {
                        fileManager.moveFile(it)
                    } else {
                        fileManager.deleteFile(it)
                    }
                }
                photoPath = null
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

        private const val REQUEST_PHOTO = 500

        private const val REQUEST_LOCATION = 600
    }
}