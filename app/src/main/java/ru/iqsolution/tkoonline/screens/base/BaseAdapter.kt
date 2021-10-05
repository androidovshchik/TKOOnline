package ru.iqsolution.tkoonline.screens.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

interface AdapterListener<T> {

    fun onItemClick(position: Int, item: T)
}

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewHolder<T>>() {

    val items = mutableListOf<T>()

    private var reference: WeakReference<AdapterListener<T>>? = null

    fun setListener(listener: AdapterListener<T>) {
        reference = WeakReference(listener)
    }

    @Suppress("UNCHECKED_CAST")
    fun <R : AdapterListener<T>> getListener(): R? {
        return reference?.get() as? R
    }

    fun getAdapterListener(): AdapterListener<T>? {
        return getListener()
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