package ru.iqsolution.tkoonline.screens.containers

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_container.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.screens.BaseAdapter
import ru.iqsolution.tkoonline.screens.BaseViewHolder

class ContainersAdapter : BaseAdapter<ContainerItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_container))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder<ContainerItem>(itemView) {

        private val address: TextView = itemView.container_address

        private val range: TextView = itemView.container_range

        private val oval: View = itemView.container_oval

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: ContainerItem) {

        }
    }
}