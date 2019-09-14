/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

package ru.iqsolution.tkoonline.screens

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

private typealias Listener<T> = (position: Int, item: T, param: Any?) -> Unit

@Suppress("unused")
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewHolder<T>>() {

    @Suppress("MemberVisibilityCanBePrivate")
    val items = arrayListOf<T>()

    protected var listener: Listener<T>? = null

    fun setAdapterListener(listener: Listener<T>?) {
        this.listener = listener
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