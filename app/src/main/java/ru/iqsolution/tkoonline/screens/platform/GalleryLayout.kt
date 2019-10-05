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
import ru.iqsolution.tkoonline.extensions.activity
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType

class GalleryLayout : RelativeLayout {

    private val photoEvents = arrayListOf<PhotoEvent>()

    private var photoType = PhotoType.Default.OTHER

    private var enableShoot = false

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
        photo1.setOnClickListener {
            onClickEvent(photoEvents.getOrNull(0) ?: return@setOnClickListener)
        }
        photo2.setOnClickListener {
            onClickEvent(photoEvents.getOrNull(1) ?: return@setOnClickListener)
        }
        photo3.setOnClickListener {
            onClickEvent(photoEvents.getOrNull(2) ?: return@setOnClickListener)
        }
        photo_add.setOnClickListener {
            if (enableShoot) {
                if (photoEvents.size < 3) {
                    onClickEvent(null)
                } else {
                    context.toast("Максимум 3 фото для раздела")
                }
            }
        }
    }

    private fun onClickEvent(photoEvent: PhotoEvent?) {
        context.activity()?.let {
            if (it is GalleryListener && !it.isFinishing) {
                it.onPhotoClick(photoType, photoEvent)
            }
        }
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.GalleryLayout).use { a ->
                photoType = PhotoType.Default.fromId(a.getInt(R.styleable.GalleryLayout_photoType, -1))
            }
        }
        subtitle.text = context.getString(
            R.string.platform_gallery,
            photoType.description.toLowerCase()
        )
        mark.background = ContextCompat.getDrawable(
            context, when (photoType) {
                PhotoType.Default.BEFORE -> R.drawable.ic_rect_green
                PhotoType.Default.AFTER -> R.drawable.ic_rect_red
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