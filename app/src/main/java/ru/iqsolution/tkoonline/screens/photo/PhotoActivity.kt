package ru.iqsolution.tkoonline.screens.photo

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.EXTRA_PATH
import ru.iqsolution.tkoonline.EXTRA_TITLE
import ru.iqsolution.tkoonline.GlideApp
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseActivity

class PhotoActivity : BaseActivity<PhotoPresenter>(), PhotoContract.View {

    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        presenter = PhotoPresenter(application).apply {
            attachView(this@PhotoActivity)
        }
        toolbar_back.onClick {
            finish()
        }
        toolbar_title.text = intent.getStringExtra(EXTRA_TITLE)
        val path = intent.getStringExtra(EXTRA_PATH)
        GlideApp.with(applicationContext)
            .load(path)
            .into(photo_preview)
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

    override fun onBackPressed() {}

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
                photoPath = null
            }
        }
    }

    companion object {

        private const val REQUEST_PHOTO = 5000
    }
}