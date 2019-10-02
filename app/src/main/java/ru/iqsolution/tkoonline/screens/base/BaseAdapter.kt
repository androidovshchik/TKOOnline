package ru.iqsolution.tkoonline.screens.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewHolder<T>>() {

    val items = arrayListOf<T>()

    protected var reference: WeakReference<AdapterListener<T>>? = null

    fun setAdapterListener(listener: AdapterListener<T>) {
        reference = WeakReference(listener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.onBindItem(position, items[position])
    }

    override fun getItemCount() = items.size
}

@Suppress("UNUSED_PARAMETER", "unused")
abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun onBindItem(position: Int, item: T)

    val appContext: Context
        get() = itemView.context.applicationContext
}