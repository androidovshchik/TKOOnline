package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.FileProvider
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.services.TelemetryService

@SuppressLint("Registered")
open class BaseActivity<T : BasePresenter<*>> : Activity(), IBaseView {

    open val attachService = false

    protected lateinit var presenter: T

    protected var telemetryService: TelemetryService? = null

    private var waitDialog: WaitDialog? = null

    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStart() {
        super.onStart()
        if (attachService) {
            bindTasksService()
        }
    }

    private fun bindTasksService() {
        if (TasksService.launch(preferences)) {
            if (telemetryService == null) {
                bindService(intentFor<TelemetryService>(), telemetryConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val file = presenter.createPhoto()
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
        if (requestCode == REQUEST_PHOTO) {
            photoPath?.let {
                if (resultCode == RESULT_OK) {
                    presenter.movePhoto(it)
                } else {
                    presenter.deletePhoto(it)
                }
            }
        }
    }

    private fun unbindTasksService() {
        if (telemetryService != null) {
            unbindService(telemetryConnection)
            telemetryService = null
        }
    }

    override fun onStop() {
        if (attachService) {
            unbindTasksService()
        }
        super.onStop()
    }

    private val telemetryConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            telemetryService = (binder as TelemetryService.Binder).service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            telemetryService = null
        }
    }

    override fun onDestroy() {
        waitDialog?.dismiss()
        presenter.detachView()
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_PHOTO = 500
    }
}