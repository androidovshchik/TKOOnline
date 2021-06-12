package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.item_container.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder

fun TextView.setValueText(text: CharSequence) {
    val smallStyle = RelativeSizeSpan(0.6f)
    val colorStyle = ForegroundColorSpan(context.getColor(R.color.colorTextGrayDark))
    setText(SpannableStringBuilder(text).apply {
        setSpan(colorStyle, 0, text.length - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(smallStyle, text.length - 2, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    })
}

class ContainerAdapter : BaseAdapter<Container>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_container))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder<Container>(itemView) {

        private val icon: ImageView = itemView.icon_type

        private val type: TextView = itemView.text_type

        private val volume: TextView = itemView.volume_value

        private val count: TextView = itemView.count_value

        private val upCount: ImageView = itemView.arrow_up_count

        private val downCount: ImageView = itemView.arrow_down_count

        init {
            upCount.setOnClickListener {

                reference.get()?.apply {
                    if (containerCount < 99) {
                        containerCount++
                        updateCountText()
                    }
                }
            }
            downCount.setOnClickListener {
                reference.get()?.apply {
                    if (containerCount > 0) {
                        containerCount--
                        updateCountText()
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Container) {
            val containerType = item.toContainerType()
            val isBulk = containerType == ContainerType.BULK1 || containerType == ContainerType.BULK2
            icon.setImageResource(containerType.icon)
            type.text = containerType.shortName
            volume.setValueText(appContext.getString(R.string.platform_volume, item.containerVolume))
            count.setValueText(appContext.getString(R.string.platform_count, item.containerCount))
            upCount.isInvisible = isBulk
            downCount.isInvisible = isBulk
        }
    }
}