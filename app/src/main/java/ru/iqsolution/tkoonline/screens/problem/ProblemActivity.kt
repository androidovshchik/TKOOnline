package ru.iqsolution.tkoonline.screens.problem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_problem.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity

/**
 * Returns [android.app.Activity.RESULT_OK] if photo event was saved
 */
class ProblemActivity : BaseActivity<ProblemPresenter>(), ProblemContract.View {

    private lateinit var platform: PlatformContainers

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        presenter = ProblemPresenter().also {
            it.attachView(this)
            platform = it.fromJson(intent.getStringExtra(EXTRA_PROBLEM_PLATFORM), PlatformContainers::class.java)
        }
        toolbar_back.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        toolbar_title.text = platform.address
        platform_id.setTextBoldSpan(getString(R.string.platform_id, platform.kpId), 0, 3)
        platform_range.setTextBoldSpan(
            getString(
                R.string.platform_range,
                platform.timeLimitFrom.toString(FORMAT_TIME),
                platform.timeLimitTo.toString(FORMAT_TIME)
            ), 2, 7, 11, 16
        )
        val photoTypes = intent.getSerializableExtra(EXTRA_PROBLEM_PHOTO_TYPES) as ArrayList<PhotoType>
        photoTypes.forEach {
            if (it.isError == 1) {
                addButton(it)
            }
        }
    }

    private fun addButton(photoType: PhotoType) {
        val button = View.inflate(applicationContext, R.layout.item_problem, null) as Button
        problem_content.addView(button.apply {
            text = photoType.description
            setOnClickListener {
                startActivityNoop<PhotoActivity>(
                    REQUEST_PHOTO,
                    EXTRA_PHOTO_TITLE to photoType.description,
                    EXTRA_PHOTO_EVENT to PhotoEvent(platform.kpId, photoType.id),
                    EXTRA_PHOTO_LINKED_IDS to platform.allLinkedIds
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

    override fun onBackPressed() {}

    companion object {

        private const val REQUEST_PHOTO = 500
    }
}