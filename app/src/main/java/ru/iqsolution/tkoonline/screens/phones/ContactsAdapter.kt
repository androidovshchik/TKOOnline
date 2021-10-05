package ru.iqsolution.tkoonline.screens.phones

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_phone.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.ifNullOrBlank
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder
import java.time.LocalDate

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
                    getAdapterListener()?.onItemClick(position, items[position])
                } catch (e: Throwable) {
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Contact) {
            name.text = item.name.ifNullOrBlank { "Неизвестный" }
            phone.text = item.phone
            val today = LocalDate.now()
            if (today.equals(item.whenLogged?.toLocalDate())) {
                image.setBackgroundResource(R.drawable.oval_green)
            } else {
                image.setBackgroundResource(R.drawable.oval_dark_gray)
            }
        }
    }
}