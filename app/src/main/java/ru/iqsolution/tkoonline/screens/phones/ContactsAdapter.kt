package ru.iqsolution.tkoonline.screens.phones

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_phone.view.*
import org.joda.time.DateTime
import org.joda.time.Duration
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.ifNullOrBlank
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder
import java.util.concurrent.TimeUnit

class ContactsAdapter : BaseAdapter<Contact>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_phone))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder<Contact>(itemView) {

        private val name: TextView = itemView.tv_name

        private val phone: TextView = itemView.tv_phone

        private val image: ImageView = itemView.iv_phone

        init {
            itemView.setOnClickListener {
                try {
                    val position = bindingAdapterPosition
                    reference?.get()?.onAdapterEvent(position, items[position])
                } catch (e: Throwable) {
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Contact) {
            name.text = item.name.ifNullOrBlank { "Неизвестный" }
            phone.text = item.phone
            val now = DateTime.now()
            val online = item.whenLogged
            val delay = if (online != null) Duration(online, now).millis else Long.MAX_VALUE
            when {
                delay > TimeUnit.DAYS.toMillis(1) ->
                    image.setBackgroundResource(R.drawable.oval_dark_gray)
                delay > TimeUnit.MINUTES.toMillis(30) ->
                    image.setBackgroundResource(R.drawable.oval_yellow)
                else -> image.setBackgroundResource(R.drawable.oval_green)
            }
        }
    }
}