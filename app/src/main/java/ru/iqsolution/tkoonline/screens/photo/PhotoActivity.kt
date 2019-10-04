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
import timber.log.Timber
import java.io.File

/**
 * Returns [android.app.Activity.RESULT_OK] if photo event was saved
 */
class PhotoActivity : BaseActivity<PhotoPresenter>(), PhotoContract.View {

    private lateinit var photoEvent: PhotoEvent

    private lateinit var externalPhoto: File

    private var preFinishing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        photoEvent = intent.getSerializableExtra(EXTRA_PHOTO_EVENT) as PhotoEvent
        presenter = PhotoPresenter().also {
            it.attachView(this)
            externalPhoto = it.getExternalFile(photoEvent)
        }
        toolbar_back.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            closePreview(RESULT_CANCELED)
        }
        toolbar_title.text = intent.getStringExtra(EXTRA_PHOTO_TITLE) ?: PhotoType.Default.OTHER.description
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
            presenter.saveEvent(photoEvent, externalPhoto)
        }
        if (photoEvent.sent) {
            photo_delete.isEnabled = false
            photo_retake.isEnabled = false
            photo_save.isEnabled = false
        }
        if (photoEvent.id != null) {
            showPhoto(photoEvent)
        } else {
            takePhoto()
        }
    }

    override fun closePreview(result: Int) {
        preFinishing = true
        setResult(result)
        finish()
    }

    private fun showPhoto(instance: Any) {
        GlideApp.with(applicationContext)
            .load(instance)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(photo_preview)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent.apply {
                putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(applicationContext, "$packageName.fileprovider", externalPhoto)
                )
            }, REQUEST_PHOTO)
        } else {
            toast("Не найдено приложение для фото")
            closePreview(RESULT_CANCELED)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO -> {
                when {
                    resultCode == RESULT_OK -> showPhoto(externalPhoto)
                    photoEvent.id == null -> closePreview(RESULT_CANCELED)
                }
            }
        }
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        try {
            externalPhoto.delete()
        } catch (e: Throwable) {
            Timber.e(e)
        }
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_PHOTO = 600
    }
}