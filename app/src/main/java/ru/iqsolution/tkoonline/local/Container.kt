package ru.iqsolution.tkoonline.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.models.ContainerType

@Entity(tableName = "containers")
class Container() {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @SerializedName("container_type")
    lateinit var containerType: ContainerType

    @SerializedName("container_type_volume")
    var containerVolume = 0f

    @SerializedName("container_count")
    var containerCount = 0

    val isEmpty: Boolean
        get() = containerVolume < 0.1f

    constructor(type: ContainerType) : this() {
        containerType = type
    }

    fun addFrom(container: ru.iqsolution.tkoonline.local.Container) {
        if (isEmpty) {
            containerVolume = container.containerVolume
        }
        containerCount += container.containerCount
    }
}