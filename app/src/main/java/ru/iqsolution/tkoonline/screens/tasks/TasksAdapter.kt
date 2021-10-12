package ru.iqsolution.tkoonline.screens.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_platform.view.*
import org.jetbrains.anko.dip
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.local.entities.Task
import ru.iqsolution.tkoonline.patternTime
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder

class TasksAdapter(context: Context) : BaseAdapter<Task>() {

    val primaryItems = mutableListOf<Task>()

    private val minSize = context.dip(17)

    private val maxSize = context.dip(30)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_platform))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Task>, position: Int) {
        val item = when {
            position < primaryItems.size -> primaryItems[position]
            else -> items[position - primaryItems.size]
        }
        holder.onBindItem(position, item)
    }

    override fun getItemCount() = primaryItems.size + items.size

    inner class ViewHolder(itemView: View) : BaseViewHolder<Task>(itemView) {

        private val address: TextView = itemView.container_address

        private val range: TextView = itemView.container_range

        private val oval: View = itemView.container_oval

        init {
            itemView.setOnClickListener {
                try {
                    val position = bindingAdapterPosition
                    val item = when {
                        position < primaryItems.size -> primaryItems[position]
                        else -> items[position - primaryItems.size]
                    }
                    getAdapterListener()?.onItemClick(position, item)
                } catch (e: Throwable) {
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Task) {
            val size = if (item.meters < 80) maxSize else minSize
            itemView.setBackgroundColor(if (item.highlighted) HIGHLIGHT else Color.WHITE)
            address.text = item.address
            range.text = appContext.getString(
                R.string.platform_range_n,
                item.timeLimitFrom.format(patternTime),
                item.timeLimitTo.format(patternTime)
            )
            oval.apply {
                layoutParams.apply {
                    height = size
                    width = size
                }
                setBackgroundResource(item.toPlatformStatus().drawable)
            }
        }
    }

    companion object {

        private const val HIGHLIGHT = 0x804D4A5B.toInt()
    }
}