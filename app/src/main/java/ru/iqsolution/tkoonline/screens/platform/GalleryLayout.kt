package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_photo.view.*
import kotlinx.android.synthetic.main.merge_gallery.view.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.GlideApp
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.activity
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType

class GalleryLayout : RelativeLayout {

    private val photoEvents = arrayListOf<PhotoEvent>()

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.let {
            init(it)
        }
    }

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(attrs)
    }

    init {
        View.inflate(context, R.layout.merge_gallery, this)
        photo1.onClick {
            onClickEvent(photoEvents.getOrNull(0) ?: return@onClick)
        }
        photo2.onClick {
            onClickEvent(photoEvents.getOrNull(1) ?: return@onClick)
        }
        photo3.onClick {
            onClickEvent(photoEvents.getOrNull(2) ?: return@onClick)
        }
        photo_add.onClick {
            onClickEvent(null)
        }
    }

    private fun onClickEvent(photoEvent: PhotoEvent?) {
        context.activity()?.let {
            if (it is GalleryListener && !it.isFinishing) {
                it.onPhotoClick(photoEvent)
            }
        }
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        var id = 0
        var title = "Фотографии"
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.GalleryLayout).use { a ->
                id = when (PhotoType.Default.fromId(a.getInt(R.styleable.GalleryLayout_photoType, -1))) {
                    PhotoType.Default.BEFORE -> {
                        title = context.getString(
                            R.string.platform_gallery,
                            PhotoType.Default.BEFORE.description.toLowerCase()
                        )
                        R.drawable.ic_rect_green
                    }
                    PhotoType.Default.AFTER -> {
                        title = context.getString(
                            R.string.platform_gallery,
                            PhotoType.Default.AFTER.description.toLowerCase()
                        )
                        R.drawable.ic_rect_red
                    }
                    else -> 0
                }
            }
        }
        subtitle.text = title
        mark.background = ContextCompat.getDrawable(context, id)
    }

    fun updatePhotos(events: List<PhotoEvent>) {
        photoEvents.apply {
            clear()
            addAll(events)
        }
        photo1.photo.updatePhoto(events.getOrNull(0))
        photo2.photo.updatePhoto(events.getOrNull(1))
        photo3.photo.updatePhoto(events.getOrNull(2))
    }

    override fun hasOverlappingRendering() = false

    private fun ImageView.updatePhoto(photoEvent: PhotoEvent?) {
        photoEvent?.let {
            background = ContextCompat.getDrawable(context, R.drawable.photo_oval_dark)
            GlideApp.with(context)
                .load(it)
                .into(this)
        } ?: run {
            background = ContextCompat.getDrawable(context, R.drawable.photo_oval_light)
            setImageResource(R.drawable.ic_camera_white)
        }
    }
}