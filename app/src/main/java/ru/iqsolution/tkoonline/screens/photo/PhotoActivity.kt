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
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import java.io.File

class PhotoActivity : BaseActivity<PhotoPresenter>(), PhotoContract.View {

    private lateinit var fileManager: FileManager

    private lateinit var photoEvent: PhotoEvent

    private lateinit var externalPhoto: File

    private lateinit var internalPhoto: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        presenter = PhotoPresenter(application).apply {
            attachView(this@PhotoActivity)
        }
        fileManager = FileManager(applicationContext)
        photoEvent = intent.getSerializableExtra(EXTRA_PHOTO_EVENT) as PhotoEvent
        toolbar_back.setOnClickListener {
            fileManager.deleteFile(externalPhoto)
            setResult(RESULT_CANCELED)
            finish()
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
        if (photoEvent.id != null) {
            internalPhoto = File(photoEvent.path)
            externalPhoto = File(fileManager.externalDir, internalPhoto.name)
            showPhoto()
        } else {
            externalPhoto = fileManager.createFile()
            internalPhoto = File(fileManager.photosDir, externalPhoto.name)
            takePhoto()
        }
    }

    override fun closeScreen() {
        finish()
    }

    //externalPhoto = File(fileManager.externalDir, internalPhoto?.name)
    private fun takePhoto(path: String? = null) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            photoPath = path ?: fileManager.createFile().path
            val uri = FileProvider.getUriForFile(applicationContext, "$packageName.fileprovider", file)
            startActivityForResult(intent.apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }, REQUEST_PHOTO)
        } else {
            toast("Не найдено приложение для фото")
            finish()
        }
    }

    private fun showPhoto() {
        GlideApp.with(applicationContext)
            .load(internalPhoto)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(photo_preview)
    }

    override fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO -> {
                outerPhoto?.also {
                    if (resultCode == RESULT_OK) {
                        fileManager.copyFile(it)
                    } else {
                        fileManager.deleteFile(it)
                    }
                }
                photoPath = null
            }
        }
    }

    companion object {

        private const val REQUEST_PHOTO = 600
    }
}