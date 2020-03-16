package ru.iqsolution.tkoonline.screens.outside

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_outside.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.EXTRA_PHOTO_EVENT
import ru.iqsolution.tkoonline.EXTRA_PHOTO_TITLE
import ru.iqsolution.tkoonline.EXTRA_PHOTO_TYPES
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity

/**
 * Returns [android.app.Activity.RESULT_OK] if photo event was saved
 */
class OutsideActivity : BaseActivity<OutsideContract.Presenter>(), OutsideContract.View {

    override val presenter: OutsidePresenter by instance()

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outside)
        toolbar_back.setOnClickListener {
            onBackPressed()
        }
        toolbar_title.text = "Фото вне справочника"
        val photoTypes = intent.getSerializableExtra(EXTRA_PHOTO_TYPES) as ArrayList<PhotoType>
        addButton(PhotoType.Default.BEFORE.toType())
        addButton(PhotoType.Default.AFTER.toType())
        photoTypes.forEach {
            if (it.isError == 1) {
                addButton(it)
            }
        }
    }

    private fun addButton(photoType: PhotoType) {
        val button = View.inflate(applicationContext, R.layout.item_problem, null) as Button
        outside_content.addView(button.apply {
            layoutParams = LinearLayout.LayoutParams(matchParent, dip(53)).also {
                it.setMargins(dip(20), 0, dip(20), dip(20))
            }
            text = photoType.description
            setBackgroundResource(if (photoType.isError == 1) R.drawable.button_gray else R.drawable.button_green)
            setOnClickListener {
                startActivityNoop<PhotoActivity>(
                    REQUEST_PHOTO,
                    EXTRA_PHOTO_TITLE to photoType.description,
                    EXTRA_PHOTO_EVENT to PhotoEvent(photoType.id).apply {
                        ready = true
                    }
                )
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    companion object {

        private const val REQUEST_PHOTO = 700
    }
}