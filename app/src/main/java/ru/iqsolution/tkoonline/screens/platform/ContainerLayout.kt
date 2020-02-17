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
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import java.lang.ref.WeakReference

class ContainerLayout : RelativeLayout {

    private lateinit var reference: WeakReference<Container>

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
        arrow_up_count.setOnClickListener {
            reference.get()?.apply {
                if (containerCount < 99) {
                    containerCount++
                    updateCountText()
                }
            }
        }
        arrow_down_count.setOnClickListener {
            reference.get()?.apply {
                if (containerCount > 0) {
                    containerCount--
                    updateCountText()
                }
            }
        }
    }

    fun setContainer(container: Container) {
        reference = WeakReference(container)
        val containerType = container.toContainerType()
        icon_type.setImageResource(containerType.icon)
        text_type.text = containerType.shortName
        updateVolumeText()
        if (containerType != ContainerType.BULK1 && containerType != ContainerType.BULK2) {
            arrow_up_count.visibility = VISIBLE
            arrow_down_count.visibility = VISIBLE
            count_value.visibility = VISIBLE
        }
        updateCountText()
    }

    fun updateContainer(container: Container) {
        reference.get()?.let {
            if (it.kpId == container.kpId) {
                it.containerCount = container.containerCount
                updateCountText()
            }
        }
    }

    private fun updateVolumeText() {
        volume_value.setValueText(context.getString(R.string.platform_volume, reference.get()?.containerVolume ?: 0f))
    }

    private fun updateCountText() {
        count_value.setValueText(context.getString(R.string.platform_count, reference.get()?.containerCount ?: 0))
    }

    fun clear() {
        reference.clear()
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