package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.merge_container.view.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.use
import ru.iqsolution.tkoonline.models.ContainerType

class ContainerLayout : LinearLayout {

    private var containerType = ContainerType.UNKNOWN

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

        }
        arrow_down_volume.onClick {

        }
        arrow_up_count.onClick {

        }
        arrow_down_count.onClick {

        }
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.ContainerLayout).use { a ->
                containerType = ContainerType.fromId(a.getString(R.styleable.ContainerLayout_containerType))
            }
        }
        icon_type.setImageResource(containerType.icon)
        text_type.text = containerType.shortName
    }

    override fun hasOverlappingRendering() = false

    fun TextView.setTextBoldSpan(text: CharSequence, indices: List<Int>) {
        require(indices.size % 2 == 0) { "The size of list must be an even number" }
        val boldStyle = StyleSpan(Typeface.BOLD)
        setText(SpannableStringBuilder(text).apply {
            indices.chunked(2).forEach {
                setSpan(boldStyle, it[0], it[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        })
    }
}