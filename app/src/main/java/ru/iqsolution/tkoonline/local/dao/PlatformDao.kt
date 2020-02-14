package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import ru.iqsolution.tkoonline.local.entities.Platform

@Dao
interface PlatformDao {

    @Insert
    fun insert(item: List<Platform>)
}