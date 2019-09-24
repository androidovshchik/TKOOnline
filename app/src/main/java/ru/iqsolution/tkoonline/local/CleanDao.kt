package ru.iqsolution.tkoonline.local

import androidx.room.*

@Dao
interface CleanDao {

    @Transaction
    @Insert
    fun insert(vararg items: ru.iqsolution.tkoonline.local.CleanEvent)

    @Transaction
    @Delete
    fun delete(vararg items: ru.iqsolution.tkoonline.local.CleanEvent)

    @Query("SELECT * FROM cleanup ORDER BY id DESC")
    fun getAllEvents(): List<ru.iqsolution.tkoonline.local.CleanEvent>
}