package ru.iqsolution.tkoonline.screens.photo

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.bumptech.glide.signature.ObjectKey
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.EXTRA_PHOTO_EVENT
import ru.iqsolution.tkoonline.EXTRA_PHOTO_TITLE
import ru.iqsolution.tkoonline.GlideApp
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import java.io.File

/**
 * Returns [android.app.Activity.RESULT_OK] if photo event was saved
 */
class PhotoActivity : BaseActivity<PhotoPresenter>(), PhotoContract.View {

    private lateinit var photoEvent: PhotoEvent

    override lateinit var externalPhoto: File

    override lateinit var internalPhoto: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        photoEvent = intent.getSerializableExtra(EXTRA_PHOTO_EVENT) as PhotoEvent
        presenter = PhotoPresenter(application).apply {
            attachView(this@PhotoActivity)
            initEvent(photoEvent)
        }
        toolbar_back.setOnClickListener {
            closePreview(RESULT_CANCELED)
        }
        toolbar_title.text = intent.getStringExtra(EXTRA_PHOTO_TITLE) ?: PhotoType.Default.OTHER.description
        photo_delete.setOnClickListener {
            presenter.deleteEvent(photoEvent)
        }
        photo_retake.setOnClickListener {
            takePhoto()
        }
        photo_save.setOnClickListener {
            presenter.saveEvent(photoEvent)
        }
        if (photoEvent.sent) {
            photo_delete.isEnabled = false
            photo_retake.isEnabled = false
            photo_save.isEnabled = false
        }
    }

    override fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val uri = FileProvider.getUriForFile(applicationContext, "$packageName.fileprovider", externalPhoto)
            startActivityForResult(intent.apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }, REQUEST_PHOTO)
        } else {
            toast("Не найдено приложение для фото")
            closePreview(RESULT_CANCELED)
        }
    }

    override fun showPhoto() {
        GlideApp.with(applicationContext)
            .load(internalPhoto)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(photo_preview)
    }

    /**
     * Called from different threads
     */
    override fun closePreview(result: Int) {
        setResult(result)
        finish()
    }

    override fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    photo_preview.setImageResource(0)
                    presenter.movePhoto(externalPhoto.path, internalPhoto.path)
                }
            }
        }
    }

    companion object {

        private const val REQUEST_PHOTO = 600
    }
}