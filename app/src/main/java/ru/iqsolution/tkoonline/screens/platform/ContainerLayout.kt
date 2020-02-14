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
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.merge_container.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.SimpleContainer

class ContainerLayout : RelativeLayout {

    var containerType = ContainerType.UNKNOWN

    var container: SimpleContainer? = null
        set(value) {
            field = value
            updateVolumeText()
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
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.ContainerLayout).use {
                containerType = ContainerType.fromId(getString(R.styleable.ContainerLayout_containerType))
            }
        }
        arrow_up_count.setOnClickListener {
            container?.apply {
                if (containerCount < 99) {
                    containerCount++
                    updateCountText()
                }
            }
        }
        arrow_down_count.setOnClickListener {
            container?.apply {
                if (containerCount > 0) {
                    containerCount--
                    updateCountText()
                }
            }
        }
        if (containerType == ContainerType.BULK1 || containerType == ContainerType.BULK2) {
            arrow_up_count.visibility = INVISIBLE
            arrow_down_count.visibility = INVISIBLE
            count_value.visibility = INVISIBLE
        }
        icon_type.setImageResource(containerType.icon)
        text_type.text = containerType.shortName
    }

    fun updateContainer(type: String, containers: List<SimpleContainer>) {
        containers.forEach {
            if (containerType.id == it.containerType) {
                if (it.linkedIds.isNotEmpty() || containerType.id == type) {
                    container = it
                    visibility = View.VISIBLE
                    return
                }
            }
        }
        container = null
        visibility = View.GONE
    }

    private fun updateVolumeText() {
        volume_value.setValueText(context.getString(R.string.platform_volume, container?.containerVolume ?: 0f))
    }

    private fun updateCountText() {
        count_value.setValueText(context.getString(R.string.platform_count, container?.containerCount ?: 0))
    }

    override fun hasOverlappingRendering() = false

    fun clear() {
        container = null
    }

    private fun TextView.setValueText(text: CharSequence) {
        val smallStyle = RelativeSizeSpan(0.6f)
        val colorStyle = ForegroundColorSpan(context.getColor(R.color.colorTextGrayDark))
        setText(SpannableStringBuilder(text).apply {
            setSpan(colorStyle, 0, text.length - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(smallStyle, text.length - 2, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
    }
}