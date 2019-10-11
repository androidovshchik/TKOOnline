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
import androidx.core.view.children
import coil.api.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.merge_gallery.view.*
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.makeCallback
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType

class GalleryLayout : RelativeLayout {

    private var photoType = PhotoType.Default.OTHER

    private val photoEvents = arrayListOf<PhotoEvent>()

    private var enableShoot = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
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

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.merge_gallery, this)
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.GalleryLayout).use {
                photoType = PhotoType.Default.fromId(getInt(R.styleable.GalleryLayout_photoType, -1))
            }
        }
        photo_add.setOnClickListener {
            if (enableShoot) {
                if (photoEvents.size < 3) {
                    makeCallback<GalleryListener> {
                        onPhotoClick(photoType, null)
                    }
                } else {
                    context.toast("Доступно не более 3 фото")
                }
            }
        }
        subtitle.text = context.getString(
            R.string.platform_gallery,
            photoType.description.toLowerCase()
        )
        mark.background = ContextCompat.getDrawable(
            context, when (photoType) {
                PhotoType.Default.BEFORE -> R.drawable.ic_rect_red
                PhotoType.Default.AFTER -> R.drawable.ic_rect_green
                else -> 0
            }
        )
    }

    fun updatePhotos(events: List<PhotoEvent>) {
        photoEvents.apply {
            clear()
            events.forEach {
                if (it.type == photoType.id) {
                    add(it)
                }
            }
        }
        enableShoot = true
        gallery.children.forEachIndexed { i, view ->
            (photo1 as ImageView).updatePhoto(photoEvents.getOrNull(0))
        }
        photo3.setOnClickListener {
            photoEvents.getOrNull(2)?.let {
                makeCallback<GalleryListener> {
                    onPhotoClick(photoType, it)
                }
            }
        }
    }

    override fun hasOverlappingRendering() = false

    private fun ImageView.updatePhoto(photoEvent: PhotoEvent?) {
        photoEvent?.let {
            load(it.toFile()) {
                transformations(CircleCropTransformation())
            }
            ContextCompat.getDrawable(context, R.drawable.photo_oval_dark)
        }
    }
}