package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Platform

@Dao
interface PlatformDao {

    @Query("SELECT * FROM tokens")
    fun getAllTokens(): List<Platform>

    @Transaction
    @Insert
    fun insert(vararg items: Platform)

    @Transaction
    @Delete
    fun delete(vararg items: Platform)
}