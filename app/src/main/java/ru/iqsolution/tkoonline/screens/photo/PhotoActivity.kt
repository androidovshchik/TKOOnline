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
        presenter = PhotoPresenter(application).apply {
            attachView(this@PhotoActivity)
            externalPhoto = initEvent(photoEvent)
        }
        toolbar_back.setOnClickListener {
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
            takePhoto(externalPhoto)
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
    }

    override fun takePhoto(file: File) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent.apply {
                putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(applicationContext, "$packageName.fileprovider", file)
                )
            }, REQUEST_PHOTO)
        } else {
            toast("Не найдено приложение для фото")
            closePreview(RESULT_CANCELED)
        }
    }

    override fun showPhoto(file: File) {
        if (isFinishing) {
            return
        }
        GlideApp.with(applicationContext)
            .load(file)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(photo_preview)
    }

    override fun closePreview(result: Int) {
        if (isFinishing) {
            return
        }
        try {
            externalPhoto.delete()
        } catch (e: Exception) {
            Timber.e(e)
        }
        setResult(result)
        finish()
    }

    override fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    showPhoto(externalPhoto)
                }
            }
        }
    }

    companion object {

        private const val REQUEST_PHOTO = 600
    }
}