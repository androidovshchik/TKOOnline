package ru.iqsolution.tkoonline.screens.platforms

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_platform.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import ru.iqsolution.tkoonline.FORMAT_TIME
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder

class PlatformsAdapter(context: Context) : BaseAdapter<PlatformContainers>() {

    val primaryItems = arrayListOf<PlatformContainers>()

    private val minSize = context.dip(17)

    private val maxSize = context.dip(30)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_platform))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<PlatformContainers>, position: Int) {
        val item = when {
            position < primaryItems.size -> primaryItems[position]
            else -> items[position - primaryItems.size]
        }
        holder.onBindItem(position, item)
    }

    override fun getItemCount() = primaryItems.size + items.size

    inner class ViewHolder(itemView: View) : BaseViewHolder<PlatformContainers>(itemView) {

        private val address: TextView = itemView.container_address

        private val range: TextView = itemView.container_range

        private val oval: View = itemView.container_oval

        init {
            itemView.setOnClickListener {
                val item = when {
                    adapterPosition < primaryItems.size -> primaryItems[adapterPosition]
                    else -> items[adapterPosition - primaryItems.size]
                }
                reference?.get()?.onAdapterEvent(adapterPosition, item)
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: PlatformContainers) {
            val size = if (item.meters < 80) maxSize else minSize
            address.text = item.address
            range.text = appContext.getString(
                R.string.platform_range_n,
                item.timeLimitFrom.toString(FORMAT_TIME),
                item.timeLimitTo.toString(FORMAT_TIME)
            )
            oval.apply {
                layoutParams.apply {
                    height = size
                    width = size
                }
                backgroundDrawable = ContextCompat.getDrawable(appContext, item.status.drawable)
            }
        }
    }
}