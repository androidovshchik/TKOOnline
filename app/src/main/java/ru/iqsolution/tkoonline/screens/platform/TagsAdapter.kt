package ru.iqsolution.tkoonline.screens.platform

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_tag.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.local.entities.TagEvent
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder
import ru.iqsolution.tkoonline.screens.platform.ContainerLayout.Companion.setValueText

class TagsAdapter : BaseAdapter<TagEvent>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_tag))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder<TagEvent>(itemView) {

        private val icon: ImageView = itemView.iv_type

        private val type: TextView = itemView.tv_type

        private val volume: TextView = itemView.tv_volume

        private val number: TextView = itemView.tv_number

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: TagEvent) {
            val containerType = item.toContainerType()
            icon.setImageResource(containerType.icon)
            type.text = containerType.shortName
            volume.setValueText(appContext.getString(R.string.platform_volume, item.containerVolume))
            number.text = item.id.toString()
        }
    }
}