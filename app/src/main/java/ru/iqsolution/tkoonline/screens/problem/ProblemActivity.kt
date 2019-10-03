package ru.iqsolution.tkoonline.screens.problem

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_problem.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.item_problem.view.*
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity

class ProblemActivity : BaseActivity<ProblemPresenter>(), ProblemContract.View {

    private lateinit var platform: PlatformContainers

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        presenter = ProblemPresenter(application).apply {
            attachView(this@ProblemActivity)
            platform = fromJson(intent.getStringExtra(EXTRA_PROBLEM_PLATFORM), PlatformContainers::class.java)
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
        val view = View.inflate(applicationContext, R.layout.item_problem, null).apply {
            problem.text = photoType.description
            setOnClickListener {
                startActivityNoop<PhotoActivity>(
                    REQUEST_PHOTO,
                    EXTRA_PHOTO_TITLE to photoType.description,
                    EXTRA_PHOTO_KP_ID to platform.kpId,
                    EXTRA_PHOTO_TYPE to photoType.id
                )
            }
        }
        problem_content.addView(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    override fun onBackPressed() {}

    companion object {

        private const val REQUEST_PHOTO = 500
    }
}