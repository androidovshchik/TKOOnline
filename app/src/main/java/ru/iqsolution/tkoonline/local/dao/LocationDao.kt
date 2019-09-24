package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.CleanEvent

@Dao
interface LocationDao {

    @Transaction
    @Insert
    fun insert(vararg items: CleanEvent)

    @Transaction
    @Delete
    fun delete(vararg items: CleanEvent)

    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getAllEvents(): List<CleanEvent>
}