package ru.iqsolution.tkoonline.data

import ru.iqsolution.tkoonline.data.models.ContainerItem

@Suppress("unused")
class Containers {

    private val items = arrayListOf<ContainerItem>()

    private val lock = Any()

    fun setItems(list: List<ContainerItem>) {
        synchronized(lock) {
            items.apply {
                clear()
                addAll(list)
            }
        }
    }

    fun setItem(item: ContainerItem) {
        synchronized(lock) {
            items.apply {
                firstOrNull { it.kpId == item.kpId }?.let {
                    remove(it)
                }
                add(item)
            }
        }
    }

    fun getItems(): List<ContainerItem> {
        synchronized(lock) {
            return items
        }
    }

    fun getItem(id: Int): ContainerItem? {
        synchronized(lock) {
            return items.firstOrNull { it.kpId == id }
        }
    }

    fun clear() {
        synchronized(lock) {
            items.clear()
        }
    }
}
