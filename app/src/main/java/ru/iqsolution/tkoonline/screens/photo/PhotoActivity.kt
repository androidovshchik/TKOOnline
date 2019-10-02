package ru.iqsolution.tkoonline.screens.photo

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import java.io.File

class PhotoActivity : BaseActivity<PhotoPresenter>(), PhotoContract.View {

    private lateinit var fileManager: FileManager

    private var platform: PlatformContainers? = null

    private var outerPhoto: File? = null

    private var innerPhoto: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        presenter = PhotoPresenter(application).apply {
            attachView(this@PhotoActivity)
            platform = platformFromJson(intent.getStringExtra(EXTRA_PLATFORM))
        }
        fileManager = FileManager(applicationContext)
        photoPath = intent.getStringExtra(EXTRA_PATH)
        toolbar_back.onClick {
            finish()
        }
        toolbar_title.text = intent.getStringExtra(EXTRA_TITLE)
        if (photoPath != null) {
            showPhoto()
        }
        photo_delete.onClick {
            setResult(RESULT_CANCELED)
            finish()
        }
        photo_retake.onClick {
            setResult(RESULT_FIRST_USER)
            finish()
        }
        photo_save.onClick {
            setResult(RESULT_OK)
            finish()
        }
        takePhoto()
    }

    private fun takePhoto(path: String? = null) {
        if (photoPath != null) {
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            photoPath = path ?: fileManager.createTempFile().path
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
            .load(innerPhoto)
            .into(photo_preview)
    }

    override fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO -> {
                outerPhoto?.also {
                    if (resultCode == RESULT_OK) {
                        fileManager.moveToInternal(it)
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