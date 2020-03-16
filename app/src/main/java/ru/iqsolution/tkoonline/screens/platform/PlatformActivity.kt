package ru.iqsolution.tkoonline.screens.platform

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.collection.SimpleArrayMap
import androidx.core.view.children
import com.google.android.gms.location.LocationSettingsStates
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.jetbrains.anko.*
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.PATTERN_TIME
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.CleanEventRelated
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.common.map.MapRect
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity
import ru.iqsolution.tkoonline.screens.problem.ProblemActivity
import ru.iqsolution.tkoonline.workers.SendWorker

/**
 * Returns [android.app.Activity.RESULT_OK] if there were changes
 */
class PlatformActivity : BaseActivity<PlatformContract.Presenter>(), PlatformContract.View {

    override val presenter: PlatformPresenter by instance()

    private val gson: Gson by instance(arg = false)

    private lateinit var platform: PlatformContainers

    private val linkedPlatforms = mutableListOf<Platform>()

    private val photoTypes = mutableListOf<PhotoType>()

    private val photoErrors = SimpleArrayMap<Int, String>()

    private var alertDialog: AlertDialog? = null

    private var hasPhotoChanges = false

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        platform = intent.getSerializableExtra(EXTRA_PLATFORM) as PlatformContainers
        photoTypes.apply {
            addAll(intent.getSerializableExtra(EXTRA_PHOTO_TYPES) as ArrayList<PhotoType>)
            forEach {
                if (it.isError == 1) {
                    photoErrors.put(it.id, it.shortName)
                }
            }
        }
        toolbar_back.setOnClickListener {
            if (hasPhotoChanges) {
                alertDialog = alert("Все данные и фотографии сделанные ранее не будут отправлены", "Вы уверены?") {
                    cancelButton {}
                    positiveButton("Выйти") {
                        closeDetails(false)
                    }
                }.show()
            } else {
                closeDetails(false)
            }
        }
        toolbar_title.text = platform.address
        platform_map.apply {
            loadUrl(URL)
            setLocation(preferences.location)
            setBounds(MapRect().apply {
                update(platform)
            })
        }
        platform_id.setTextBoldSpan(getString(R.string.platform_id, platform.kpId), 0, 3)
        platform_range.setTextBoldSpan(
            getString(
                R.string.platform_range,
                platform.timeLimitFrom.toString(PATTERN_TIME),
                platform.timeLimitTo.toString(PATTERN_TIME)
            ), 2, 7, 11, 16
        )
        platform_report.setOnClickListener {
            startActivityNoop<ProblemActivity>(
                REQUEST_PROBLEM,
                EXTRA_PLATFORM to platform,
                EXTRA_PHOTO_TYPES to photoTypes
            )
        }
        platform_not_cleaned.setOnClickListener {
            val errorMessage = when {
                gallery_after.photoEvents.size > 0 -> "Удалите фотографии после уборки или отметьте что КП убрана"
                platform.errors.size <= 0 -> "Зарегистрируйте хотя бы один тип проблемы с фото"
                else -> null
            }
            if (errorMessage != null) {
                alertDialog = alert(errorMessage, "Ошибка заполнения") {
                    okButton {}
                }.show()
            } else {
                setTouchable(false)
                platform.reset()
                presenter.savePlatformEvents(platform, linkedPlatforms.apply {
                    forEach {
                        it.reset()
                    }
                })
            }
        }
        platform_cleaned.setOnClickListener {
            val errorMessage = when {
                platform.containerCount <= 0 && (linkedPlatforms.isEmpty() || linkedPlatforms.sumBy { it.containerCount } <= 0) ->
                    "Укажите сколько контейнеров было убрано"
                gallery_before.photoEvents.size <= 0 -> "Добавьте хотя бы одно фото до уборки"
                gallery_after.photoEvents.size <= 0 -> "Добавьте хотя бы одно фото после уборки"
                else -> null
            }
            if (errorMessage != null) {
                alertDialog = alert(errorMessage, "Ошибка заполнения") {
                    okButton {}
                }.show()
            } else {
                setTouchable(false)
                presenter.savePlatformEvents(platform, linkedPlatforms)
            }
        }
        attach(ContainerLayout(applicationContext).apply {
            initContainer(platform)
        }, 2)
        setTouchable(false)
        presenter.apply {
            loadLinkedPlatforms(platform.linkedIds.toList())
            loadPhotoEvents(platform.kpId)
        }
    }

    private fun attach(layout: ContainerLayout, index: Int) {
        platform_content.addView(layout, index, LinearLayout.LayoutParams(matchParent, wrapContent).apply {
            bottomMargin = dip(9)
        })
    }

    /**
     * Called once after create
     */
    override fun onLinkedPlatforms(platforms: List<Platform>) {
        linkedPlatforms.addAll(platforms)
        platforms.forEachIndexed { index, item ->
            attach(ContainerLayout(applicationContext).apply {
                initContainer(item)
            }, 3 + index)
        }
        presenter.loadCleanEvents(platform.kpId)
    }

    /**
     * Called once after create
     */
    override fun onCleanEvents(event: CleanEventRelated) {
        platform_content.children.forEach {
            if (it is ContainerLayout) {
                it.updateContainer(event.clean)
                event.events.forEach { item ->
                    it.updateContainer(item)
                }
            }
        }
    }

    override fun onPhotoEvents(events: List<PhotoEvent>) {
        events.forEach {
            photoErrors.get(it.typeId)?.let { error ->
                platform.putError(error)
            }
        }
        platform_map.setMarkers("[${gson.toJson(platform)}]")
        gallery_before.updatePhotos(events)
        gallery_after.updatePhotos(events)
        setTouchable(true)
    }

    override fun onPhotoClick(photoType: PhotoType.Default, photoEvent: PhotoEvent?) {
        setTouchable(false)
        val event = photoEvent ?: PhotoEvent(platform.kpId, photoType.id)
        startActivityNoop<PhotoActivity>(
            REQUEST_PHOTO,
            EXTRA_PHOTO_TITLE to photoType.description,
            EXTRA_PHOTO_EVENT to event,
            EXTRA_PHOTO_IDS to platform.linkedIds.toList()
        )
    }

    override fun closeDetails(hasCleanChanges: Boolean) {
        setTouchable(false)
        setResult(
            if (hasPhotoChanges || hasCleanChanges) {
                if (hasCleanChanges) {
                    SendWorker.launch(applicationContext)
                }
                RESULT_OK
            } else {
                RESULT_CANCELED
            }
        )
        finish()
    }

    override fun onLocationState(state: LocationSettingsStates?) {
        super.onLocationState(state)
        onLocationAvailability(state?.isGpsUsable == true)
    }

    override fun onLocationAvailability(available: Boolean) {
        platform_map.changeIcon(available)
    }

    override fun onLocationResult(location: SimpleLocation) {
        platform_map.setLocation(location)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO, REQUEST_PROBLEM -> {
                if (resultCode == RESULT_OK) {
                    hasPhotoChanges = true
                    presenter.loadPhotoEvents(platform.kpId)
                } else {
                    setTouchable(true)
                }
            }
        }
    }

    override fun onDestroy() {
        alertDialog?.dismiss()
        platform_map.release()
        platform_content.children.forEach {
            if (it is ContainerLayout) {
                it.clear()
            }
        }
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_PHOTO = 400

        private const val REQUEST_PROBLEM = 410

        private const val URL = "file:///android_asset/platform.html"
    }
}