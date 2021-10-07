package ru.iqsolution.tkoonline.screens.routes

import android.annotation.SuppressLint
import android.text.format.DateUtils
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
import ru.iqsolution.tkoonline.patternDateHuman
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder
import ru.iqsolution.tkoonline.toEpochMilli
import java.time.LocalDate

class SchedulesAdapter : BaseAdapter<Any>() {

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is LocalDate) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Any> {
        return when (viewType) {
            0 -> HeaderHolder(parent.inflate(R.layout.header_route))
            else -> ItemHolder(parent.inflate(R.layout.item_route))
        }
    }

    inner class HeaderHolder(itemView: View) : BaseViewHolder<Any>(itemView) {

        private val date: TextView = itemView.tv_date

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Any) {
            item as LocalDate
            val day = DateUtils.getRelativeDateTimeString(appContext, item.toEpochMilli(), DateUtils.DAY_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, 0).toString().substringBefore(',')
            date.text = "$day (${item.format(patternDateHuman)})"
        }
    }

    inner class ItemHolder(itemView: View) : BaseViewHolder<Any>(itemView) {

        private val card: CardView = itemView.cv_route

        private val id: TextView = itemView.tv_id

        private val date: TextView = itemView.tv_when

        private val fio: TextView = itemView.tv_fio

        private val progress: TextView = itemView.tv_progress

        init {
            card.setOnClickListener {
                try {
                    val position = bindingAdapterPosition
                    getListener<Listener>()?.onRouteClick(position, items[position] as Route)
                } catch (e: Throwable) {
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Any) {
            item as Route
            card.setCardBackgroundColor(if (item.isDone) GREEN else YELLOW)
            id.text = "№ ${item.number.ifNullOrBlank { "-" }}"
            date.text = item.day.format(patternDateHuman)
            fio.text = item.fio.ifNullOrBlank { "-" }
            progress.text = "${item.waitCount}/${item.count}"
        }
    }

    interface Listener : AdapterListener<Any> {

        fun onRouteClick(position: Int, item: Route)
    }

    companion object {

        private const val GREEN = 0xff56b60e.toInt()

        private const val YELLOW = 0xffd7d07f.toInt()
    }
}