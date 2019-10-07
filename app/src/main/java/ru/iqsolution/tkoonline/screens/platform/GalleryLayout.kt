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
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.merge_gallery.view.*
import org.jetbrains.anko.toast
import ru.iqsolution.tkoonline.GlideApp
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
            context.obtainStyledAttributes(it, R.styleable.GalleryLayout).use { a ->
                photoType = PhotoType.Default.fromId(a.getInt(R.styleable.GalleryLayout_photoType, -1))
            }
        }
        photo1.setOnClickListener {
            photoEvents.getOrNull(0)?.let {
                makeCallback<GalleryListener> {
                    onPhotoClick(photoType, it)
                }
            }
        }
        photo2.setOnClickListener {
            photoEvents.getOrNull(1)?.let {
                makeCallback<GalleryListener> {
                    onPhotoClick(photoType, it)
                }
            }
        }
        photo3.setOnClickListener {
            photoEvents.getOrNull(2)?.let {
                makeCallback<GalleryListener> {
                    onPhotoClick(photoType, it)
                }
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
        (photo1 as ImageView).updatePhoto(events.getOrNull(0))
        (photo2 as ImageView).updatePhoto(events.getOrNull(1))
        (photo3 as ImageView).updatePhoto(events.getOrNull(2))
    }

    override fun hasOverlappingRendering() = false

    private fun ImageView.updatePhoto(photoEvent: PhotoEvent?) {
        background = photoEvent?.let {
            GlideApp.with(context)
                .load(it)
                .apply(RequestOptions.circleCropTransform())
                .into(this)
            ContextCompat.getDrawable(context, R.drawable.photo_oval_dark)
        } ?: run {
            setImageResource(R.drawable.ic_camera_white)
            ContextCompat.getDrawable(context, R.drawable.photo_oval_light)
        }
    }
}