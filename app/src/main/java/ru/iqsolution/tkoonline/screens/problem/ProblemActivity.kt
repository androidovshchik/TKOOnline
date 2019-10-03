package ru.iqsolution.tkoonline.screens.problem

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_problem.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.item_problem.view.*
import ru.iqsolution.tkoonline.EXTRA_PROBLEM_PHOTO_TYPES
import ru.iqsolution.tkoonline.EXTRA_PROBLEM_PLATFORM
import ru.iqsolution.tkoonline.FORMAT_TIME
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity

class ProblemActivity : BaseActivity<ProblemPresenter>(), ProblemContract.View {

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        presenter = ProblemPresenter(application).apply {
            attachView(this@ProblemActivity)
        }
        val platform = presenter.fromJson(intent.getStringExtra(EXTRA_PROBLEM_PLATFORM), PlatformContainers::class.java)
        toolbar_back.setOnClickListener {
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
            addButton(it)
        }
    }

    private fun addButton(photoType: PhotoType) {
        val view = View.inflate(applicationContext, R.layout.item_problem, null).apply {
            problem.text = photoType.description
            setOnClickListener {
                // todo extras
                startActivityNoop<PhotoActivity>()
            }
        }
        problem_content.addView(view)
    }

    override fun onBackPressed() {}
}