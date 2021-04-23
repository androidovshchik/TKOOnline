package ru.iqsolution.tkoonline.screens.problem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_problem.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.kodein.di.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.PATTERN_TIME
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.user.UserActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity

/**
 * Returns [android.app.Activity.RESULT_OK] if photo event was saved
 */
class ProblemActivity : UserActivity<ProblemContract.Presenter>(), ProblemContract.View {

    override val presenter: ProblemPresenter by instance()

    private lateinit var platform: PlatformContainers

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        platform = intent.getSerializableExtra(EXTRA_PLATFORM) as PlatformContainers
        toolbar_back.setOnClickListener {
            onBackPressed()
        }
        toolbar_title.text = platform.address
        platform_id.setTextBoldSpan(getString(R.string.platform_id, platform.kpId), 0, 3)
        platform_range.setTextBoldSpan(
            getString(
                R.string.platform_range,
                platform.timeLimitFrom.toString(PATTERN_TIME),
                platform.timeLimitTo.toString(PATTERN_TIME)
            ), 2, 7, 11, 16
        )
        val photoTypes = intent.getSerializableExtra(EXTRA_PHOTO_TYPES) as ArrayList<PhotoType>
        photoTypes.forEach {
            if (it.error == 1) {
                addButton(it)
            }
        }
    }

    private fun addButton(photoType: PhotoType) {
        val button = View.inflate(applicationContext, R.layout.item_problem, null) as Button
        problem_content.addView(button.apply {
            layoutParams = LinearLayout.LayoutParams(matchParent, dip(53)).also {
                it.setMargins(dip(20), 0, dip(20), dip(20))
            }
            text = photoType.description
            setBackgroundResource(R.drawable.button_gray)
            setOnClickListener {
                startActivityNoop<PhotoActivity>(
                    REQUEST_PHOTO,
                    EXTRA_PHOTO_TITLE to photoType.description,
                    EXTRA_PHOTO_EVENT to PhotoEvent(platform.kpId, photoType.id),
                    EXTRA_PHOTO_IDS to platform.linkedIds.toList()
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

        private const val REQUEST_PHOTO = 500
    }
}