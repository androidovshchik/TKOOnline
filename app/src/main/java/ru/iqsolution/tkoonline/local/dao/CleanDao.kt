package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.iqsolution.tkoonline.local.entities.CleanEvent

@Dao
interface CleanDao {

    @Transaction
    @Insert
    fun insert(vararg items: CleanEvent)

    @Query("SELECT * FROM cleanup ORDER BY id DESC")
    fun getAllEvents(): List<CleanEvent>
}