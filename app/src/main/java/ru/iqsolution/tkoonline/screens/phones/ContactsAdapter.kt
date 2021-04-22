package ru.iqsolution.tkoonline.screens.phones

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_phone.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.inflate
import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.base.BaseViewHolder

class ContactsAdapter(context: Context) : BaseAdapter<Contact>() {

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
                    //reference?.get()?.onAdapterEvent(position, item)
                } catch (e: Throwable) {
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: Contact) {
            name.text = item.name
            phone.text = item.phone
        }
    }
}