package ru.iqsolution.tkoonline.screens.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
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
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.screens.WaitDialog
import ru.iqsolution.tkoonline.services.LocationManager
import timber.log.Timber

@SuppressLint("Registered")
@Suppress("MemberVisibilityCanBePrivate")
open class BaseActivity<T : BasePresenter<*>> : Activity(), IBaseView {

    protected lateinit var presenter: T

    protected lateinit var fileManager: FileManager

    private var waitDialog: WaitDialog? = null

    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        fileManager = FileManager(applicationContext)
    }

    override fun onLocationState(state: LocationSettingsStates?) {

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

    fun requestLocation() {
        LocationServices.getSettingsClient(this)
            .checkLocationSettings(settingsRequest)
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

    protected fun takePhoto() {
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
            REQUEST_PHOTO -> {
                photoPath?.also {
                    if (resultCode == RESULT_OK) {
                        fileManager.moveFile(it)
                    } else {
                        fileManager.deleteFile(it)
                    }
                }
            }
            REQUEST_LOCATION -> {
                if (resultCode == RESULT_OK) {
                    // All required changes were successfully made
                    onLocationState(LocationSettingsStates.fromIntent(data))
                } else {
                    // The user was asked to change settings, but chose not to
                    onLocationState(null)
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

        private val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationManager.locationRequest)
            /**
             * Whether or not location is required by the calling app in order to continue.
             * Set this to true if location is required to continue and false if having location provides better results,
             * but is not required. This changes the wording/appearance of the dialog accordingly.
             */
            .setAlwaysShow(true)
            .build()

        private const val REQUEST_PHOTO = 500

        private const val REQUEST_LOCATION = 600
    }
}