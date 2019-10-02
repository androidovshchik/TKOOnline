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
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType

class ContainerLayout : LinearLayout, Container {

    override var containerType = ContainerType.UNKNOWN.id

    override var containerVolume = 0.0f

    override var containerCount = 0

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

    init {
        View.inflate(context, R.layout.merge_container, this)
        arrow_up_volume.onClick {
            if (containerVolume < 9.9f) {
                containerVolume += 0.1f
                volume_value.setRichText(context.getString(R.string.platform_volume, containerVolume))
            }
        }
        arrow_down_volume.onClick {
            if (containerVolume >= 0.1f) {
                containerVolume -= 0.1f
                volume_value.setRichText(context.getString(R.string.platform_volume, containerVolume))
            }
        }
        arrow_up_count.onClick {
            if (containerCount < 99) {
                containerCount++
                count_value.setRichText(context.getString(R.string.platform_count, containerCount))
            }
        }
        arrow_down_count.onClick {
            if (containerCount > 0) {
                containerCount--
                count_value.setRichText(context.getString(R.string.platform_count, containerCount))
            }
        }
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        val type = ContainerType.UNKNOWN
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.ContainerLayout).use { a ->
                containerType = ContainerType.fromId(a.getString(R.styleable.ContainerLayout_containerType))
            }
        }
        icon_type.setImageResource(containerType.icon)
        text_type.text = containerType.shortName
        if (type == ContainerType.BULK1 || type == ContainerType.BULK2) {
            arrow_up_volume.visibility = INVISIBLE
            arrow_down_volume.visibility = INVISIBLE
            arrow_up_count.visibility = INVISIBLE
            arrow_down_count.visibility = INVISIBLE
            count_value.visibility = INVISIBLE
        }
    }

    override fun addContainer(container: Container?) {
        super.addContainer(container)
        volume_value.setRichText(context.getString(R.string.platform_volume, containerVolume))
        count_value.setRichText(context.getString(R.string.platform_count, containerCount))
    }

    override fun hasOverlappingRendering() = false

    private fun TextView.setRichText(text: CharSequence) {
        val smallStyle = RelativeSizeSpan(0.8f)
        val colorStyle = ForegroundColorSpan(sd)
        setText(SpannableStringBuilder(text).apply {
            setSpan(colorStyle, 0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(smallStyle, 0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
    }
}