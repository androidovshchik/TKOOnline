package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.merge_container.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import kotlin.math.max
import kotlin.math.min

class ContainerLayout : LinearLayout, Container {

    override var containerType = ContainerType.UNKNOWN.id

    var hasChanges = false

    override var containerVolume = 0.0f
        set(value) {
            field = value
            updateVolumeText()
        }

    override var containerCount = 0
        set(value) {
            field = value
            updateCountText()
        }

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
        View.inflate(context, R.layout.merge_container, this)
        var type = ContainerType.UNKNOWN
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.ContainerLayout).use { a ->
                type = ContainerType.fromId(a.getString(R.styleable.ContainerLayout_containerType))
                containerType = type.id
            }
        }
        arrow_up_volume.setOnClickListener {
            if (containerVolume < 9.9f) {
                hasChanges = true
                containerVolume = min(9.999f, containerVolume + 0.1f)
                updateVolumeText()
            }
        }
        arrow_down_volume.setOnClickListener {
            if (containerVolume >= 0.1f) {
                hasChanges = true
                containerVolume = max(0f, containerVolume - 0.1f)
                updateVolumeText()
            }
        }
        arrow_up_count.setOnClickListener {
            if (containerCount < 99) {
                hasChanges = true
                containerCount++
                updateCountText()
            }
        }
        arrow_down_count.setOnClickListener {
            if (containerCount > 0) {
                hasChanges = true
                containerCount--
                updateCountText()
            }
        }
        if (type == ContainerType.BULK1 || type == ContainerType.BULK2) {
            arrow_up_volume.visibility = INVISIBLE
            arrow_down_volume.visibility = INVISIBLE
            arrow_up_count.visibility = INVISIBLE
            arrow_down_count.visibility = INVISIBLE
            count_value.visibility = INVISIBLE
        }
        icon_type.setImageResource(type.icon)
        text_type.text = type.shortName
    }

    private fun updateVolumeText() {
        volume_value.setValueText(context.getString(R.string.platform_volume, containerVolume))
    }

    private fun updateCountText() {
        count_value.setValueText(context.getString(R.string.platform_count, containerCount))
    }

    override fun hasOverlappingRendering() = false

    private fun TextView.setValueText(text: CharSequence) {
        val smallStyle = RelativeSizeSpan(0.6f)
        val colorStyle = ForegroundColorSpan(context.getColor(R.color.colorTextGrayDark))
        setText(SpannableStringBuilder(text).apply {
            setSpan(colorStyle, 0, text.length - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(smallStyle, text.length - 2, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
    }
}