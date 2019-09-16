package ru.iqsolution.tkoonline.data.local

import androidx.room.*
import ru.iqsolution.tkoonline.data.models.CleanEvent

@Dao
interface EventDao {

    @Transaction
    @Insert
    fun insert(vararg items: CleanEvent)

    @Transaction
    @Update
    fun update(vararg items: CleanEvent)

    @Transaction
    @Delete
    fun delete(vararg items: CleanEvent)

    @Query("SELECT * FROM Tasks LIMIT $QUERY_LIMIT OFFSET :offset")
    fun getAllTasks(offset: Int): List<CleanEvent>
}