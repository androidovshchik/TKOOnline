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
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.models.PhotoType

class GalleryLayout : RelativeLayout {

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
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        var id = 0
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.GalleryLayout).use { a ->
                id = when (PhotoType.Default.fromId(a.getInt(R.styleable.ContainerLayout_containerType, -1))) {
                    PhotoType.Default.BEFORE -> R.drawable.ic_rect_green
                    PhotoType.Default.AFTER -> R.drawable.ic_rect_red
                    else -> 0
                }
            }
        }
        mark.background = ContextCompat.getDrawable(context, id)
    }

    override fun hasOverlappingRendering() = false
}