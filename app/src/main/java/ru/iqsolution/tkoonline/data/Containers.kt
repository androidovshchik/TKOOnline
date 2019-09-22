package ru.iqsolution.tkoonline.data

import ru.iqsolution.tkoonline.data.models.ContainerItem

@Suppress("unused")
class Containers {

    /**
     * They all are with status [ru.iqsolution.tkoonline.data.models.ContainerStatus.PENDING]
     * or [ru.iqsolution.tkoonline.data.models.ContainerStatus.NOT_VISITED]
     */
    private val primaryItems = arrayListOf<ContainerItem>()

    private val secondaryItems = arrayListOf<ContainerItem>()

    private val lock = Any()

    fun setItems(primary: List<ContainerItem>, secondary: List<ContainerItem>) {
        synchronized(lock) {
            primaryItems.apply {
                clear()
                addAll(primary)
            }
            secondaryItems.apply {
                clear()
                addAll(secondary)
            }
        }
    }

    fun setPrimaryItems(list: List<ContainerItem>) {
        synchronized(lock) {
            primaryItems.apply {
                clear()
                addAll(list)
            }
        }
    }

    fun setSecondaryItems(list: List<ContainerItem>) {
        synchronized(lock) {
            secondaryItems.apply {
                clear()
                addAll(list)
            }
        }
    }

    fun addPrimaryItem(item: ContainerItem) {
        synchronized(lock) {
            primaryItems.add(item)
        }
    }

    fun addSecondaryItem(item: ContainerItem) {
        synchronized(lock) {
            secondaryItems.add(item)
        }
    }

    fun getPrimaryItems(): List<ContainerItem> {
        synchronized(lock) {
            return ArrayList(primaryItems)
        }
    }

    fun getSecondaryItems(): List<ContainerItem> {
        synchronized(lock) {
            return ArrayList(secondaryItems)
        }
    }

    fun getItem(id: Int): ContainerItem? {
        synchronized(lock) {
            val item = primaryItems.firstOrNull { it.kpId == id } ?: secondaryItems.firstOrNull { it.kpId == id }
            if (item != null) {
                return ContainerItem.copyFrom(item)
            }
            return null
        }
    }

    fun clear() {
        synchronized(lock) {
            primaryItems.clear()
            secondaryItems.clear()
        }
    }
}
