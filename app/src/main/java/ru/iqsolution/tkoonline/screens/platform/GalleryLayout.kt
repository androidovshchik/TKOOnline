package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.merge_gallery.view.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.activity
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType

class GalleryLayout : RelativeLayout {

    val photoEvents = arrayListOf<PhotoEvent>()

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
            val event = photoEvents.getOrNull(0) ?: return@onClick
            context.activity()?.let {
                if (it is GalleryListener && !it.isFinishing) {
                    it.onPhotoClick(event)
                }
            }
        }
        photo2.onClick {
            val event = photoEvents.getOrNull(1) ?: return@onClick
            context.activity()?.let {
                if (it is GalleryListener && !it.isFinishing) {
                    it.onPhotoClick(event)
                }
            }
        }
        photo3.onClick {
            val event = photoEvents.getOrNull(2) ?: return@onClick
            context.activity()?.let {
                if (it is GalleryListener && !it.isFinishing) {
                    it.onPhotoClick(event)
                }
            }
        }
        photo_add.onClick {
            context.activity()?.let {
                if (it is GalleryListener && !it.isFinishing) {
                    it.onPhotoClick(null)
                }
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

    }

    override fun hasOverlappingRendering() = false
}