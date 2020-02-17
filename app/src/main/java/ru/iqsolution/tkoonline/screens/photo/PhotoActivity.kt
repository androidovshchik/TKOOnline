package ru.iqsolution.tkoonline.screens.photo

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import coil.api.load
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.camera.CameraActivity
import java.io.File

/**
 * Returns [android.app.Activity.RESULT_OK] if photo event was saved
 */
class PhotoActivity : BaseActivity<PhotoContract.Presenter>(), PhotoContract.View {

    override val presenter: PhotoPresenter by instance()

    private val fileManager: FileManager by instance()

    private lateinit var photoEvent: PhotoEvent

    private lateinit var externalPhoto: File

    private var preFinishing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        photoEvent = intent.getSerializableExtra(EXTRA_PHOTO_EVENT) as PhotoEvent
        externalPhoto = presenter.getExternalFile(photoEvent)
        val linkedIds = intent.getIntegerArrayListExtra(EXTRA_PHOTO_LINKED_IDS).orEmpty()
        toolbar_back.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            closePreview(RESULT_CANCELED)
        }
        toolbar_title.apply {
            (layoutParams as RelativeLayout.LayoutParams).apply {
                removeRule(RelativeLayout.ALIGN_PARENT_END)
                removeRule(RelativeLayout.END_OF)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
            text = intent.getStringExtra(EXTRA_PHOTO_TITLE) ?: PhotoType.Default.OTHER.description
        }
        photo_delete.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            preFinishing = true
            presenter.deleteEvent(photoEvent)
        }
        photo_retake.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            takePhoto()
        }
        photo_save.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            preFinishing = true
            presenter.saveEvent(photoEvent, linkedIds, externalPhoto)
        }
        if (photoEvent.sent) {
            photo_delete.isEnabled = false
            photo_retake.isEnabled = false
            photo_save.isEnabled = false
            (photo_preview.layoutParams as RelativeLayout.LayoutParams).apply {
                removeRule(RelativeLayout.ABOVE)
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }
        if (photoEvent.id != null) {
            photo_preview.load(File(photoEvent.path))
        } else {
            takePhoto()
        }
    }

    override fun closePreview(result: Int) {
        preFinishing = true
        setResult(result)
        finish()
    }

    private fun takePhoto() {
        startActivityNoop<CameraActivity>(
            REQUEST_CAMERA,
            EXTRA_PHOTO_PATH to externalPhoto.path
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CAMERA -> {
                when {
                    resultCode == RESULT_OK -> photo_preview.load(externalPhoto)
                    photoEvent.id == null -> closePreview(RESULT_CANCELED)
                }
            }
        }
    }

    override fun onDestroy() {
        fileManager.deleteFile(externalPhoto)
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_CAMERA = 600
    }
}