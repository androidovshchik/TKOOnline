package ru.iqsolution.tkoonline.screens.routes

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.header_route.view.*
import kotlinx.android.synthetic.main.item_route.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.ifNullOrBlank
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.local.entities.Route
import ru.iqsolution.tkoonline.models.Schedule
import ru.iqsolution.tkoonline.patternDateHuman
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder

class SchedulesAdapter : BaseAdapter<Schedule>() {

    override fun getItemViewType(position: Int): Int {
        if (getChildPosition(position) >= 0) {
            return 1
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Schedule> {
        return when (viewType) {
            0 -> HeaderHolder(parent.inflate(R.layout.header_route))
            else -> ItemHolder(parent.inflate(R.layout.item_route))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Schedule>, position: Int) {
        holder.onBindItem(position, items[getParentPosition(position)])
    }

    override fun getItemCount(): Int {
        return items.sumOf { 1 + it.routes.size }
    }

    private fun getParentPosition(position: Int): Int {
        require(position >= 0)
        var i = 0
        for ((j, item) in items.withIndex()) {
            val size = 1 + item.routes.size
            if (position < i + size) {
                return j
            }
            i += size
        }
        throw RuntimeException()
    }

    private fun getChildPosition(position: Int): Int {
        require(position >= 0)
        var i = 0
        for (item in items) {
            val size = 1 + item.routes.size
            if (position < i + size) {
                return position - i - 1
            }
            i += size
        }
        return -1
    }

    inner class HeaderHolder(itemView: View) : BaseViewHolder<Schedule>(itemView) {

        private val date: TextView = itemView.tv_date

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Schedule) {
            date.text = item.date.format(patternDateHuman)
        }
    }

    inner class ItemHolder(itemView: View) : BaseViewHolder<Schedule>(itemView) {

        private val card: CardView = itemView.cv_route

        private val id: TextView = itemView.tv_id

        private val date: TextView = itemView.tv_when

        private val fio: TextView = itemView.tv_fio

        private val progress: TextView = itemView.tv_progress

        init {
            card.setOnClickListener {
                try {
                    val position = bindingAdapterPosition
                    val schedule = items[getParentPosition(position)]
                    val route = schedule.routes[getChildPosition(position)]
                    getListener<Listener>()?.onRouteClick(position, route)
                } catch (e: Throwable) {
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Schedule) {
            val route = item.routes[getChildPosition(position)]
            card.setCardBackgroundColor(if (route.isDone) GREEN else YELLOW)
            id.text = "№ ${route.number.ifNullOrBlank { "-" }}"
            date.text = item.date.format(patternDateHuman)
            fio.text = route.fio.ifNullOrBlank { "-" }
            progress.text = "${route.waitCount}/${route.count}"
        }
    }

    interface Listener : AdapterListener<Schedule> {

        fun onRouteClick(position: Int, item: Route)
    }

    companion object {

        private const val GREEN = 0xff56b60e.toInt()

        private const val YELLOW = 0xffd7d07f.toInt()
    }
}