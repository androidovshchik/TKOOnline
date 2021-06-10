package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.merge_tag.view.*
import org.jetbrains.anko.dip
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.entities.TagEvent
import ru.iqsolution.tkoonline.models.ContainerType

class TagLayout : ContainerLayout {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    @SuppressLint("Recycle")
    @Suppress("UNUSED_PARAMETER")
    override fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.merge_tag, this)
        setPadding(0, dip(10), 0, 0)
    }

    fun initTag(event: TagEvent) {
        val container = ContainerType.fromId(event.containerType)
        iv_type.setImageResource(container.icon)
        tv_type.text = container.shortName
        tv_volume.setValueText(context.getString(R.string.platform_volume, event.containerVolume))
        tv_number.text = event.id.toString()
    }

    override fun hasOverlappingRendering() = false
}