package ru.iqsolution.tkoonline.screens.containers

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_container.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.FORMAT_TIME
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.screens.BaseAdapter
import ru.iqsolution.tkoonline.screens.BaseViewHolder

class ContainersAdapter(context: Context) : BaseAdapter<ContainerItem>() {

    val primaryItems = arrayListOf<ContainerItem>()

    private val minSize = context.dip(17)

    private val maxSize = context.dip(30)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_container))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ContainerItem>, position: Int) {
        val item = when {
            position < primaryItems.size -> primaryItems[position]
            else -> items[position - primaryItems.size]
        }
        holder.onBindItem(position, item)
    }

    override fun getItemCount() = primaryItems.size + items.size

    inner class ViewHolder(itemView: View) : BaseViewHolder<ContainerItem>(itemView) {

        private val address: TextView = itemView.container_address

        private val range: TextView = itemView.container_range

        private val oval: View = itemView.container_oval

        init {
            itemView.onClick {
                val item = when {
                    adapterPosition < primaryItems.size -> primaryItems[adapterPosition]
                    else -> items[adapterPosition - primaryItems.size]
                }
                listenerRef?.get()?.onAdapterEvent(adapterPosition, item)
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: ContainerItem) {
            address.text = item.address
            range.text = appContext.getString(
                R.string.container_range, item.timeLimitFrom.toString(FORMAT_TIME),
                item.timeLimitTo.toString(FORMAT_TIME)
            )
            oval.apply {
                layoutParams.apply {
                    height = minSize
                    width = minSize
                }
                backgroundDrawable = ContextCompat.getDrawable(appContext, item.status.drawable)
            }
        }
    }
}