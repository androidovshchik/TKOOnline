package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import coil.api.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.merge_gallery.view.*
import org.jetbrains.anko.dip
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.makeCallback
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import kotlin.math.max

class GalleryLayout : RelativeLayout {

    private var photoType = PhotoType.Default.OTHER

    private val photoEvents = arrayListOf<PhotoEvent>()

    private var enableShoot = false

    private val photoSize = context.resources.getDimensionPixelSize(R.dimen.gallery_height)

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
                if (photoEvents.size < 4) {
                    makeCallback<GalleryListener> {
                        onPhotoClick(photoType, null)
                    }
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
        photo_add.apply {
            (layoutParams as MarginLayoutParams).marginStart = if (photoEvents.size > 0) dip(3) else dip(25)
            visibility = if (photoEvents.size >= 4) GONE else VISIBLE
        }
        gallery.apply {
            for (i in 0 until max(photoEvents.size, childCount - 1)) {
                photoEvents.getOrNull(i)?.let {
                    val child = getChildAt(i - 1) ?: View.inflate(context, R.layout.item_photo, null).apply {
                        layoutParams = LinearLayout.LayoutParams(photoSize, photoSize).also { params ->
                            params.marginStart = if (i == 0) dip(25) else dip(10)
                        }
                        setOnClickListener { _ ->
                            makeCallback<GalleryListener> {
                                onPhotoClick(photoType, it)
                            }
                        }
                        addView(this, 0)
                    }
                    (child as ImageView).load(it.toFile()) {
                        transformations(CircleCropTransformation())
                    }
                } ?: run {
                    removeViewAt(i)
                }
            }
        }
    }

    override fun hasOverlappingRendering() = false
}