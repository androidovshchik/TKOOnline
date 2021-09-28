package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.TaskType

@Dao
abstract class TaskTypeDao {

    @Query(
        """
        SELECT * FROM task_types 
        ORDER BY tt_id ASC
    """
    )
    abstract fun getAll(): List<TaskType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<TaskType>)

    @Transaction
    open fun safeInsert(items: List<TaskType>) {
        deleteAll()
        insertAll(items)
    }

    @Query(
        """
        DELETE FROM task_types
    """
    )
    abstract fun deleteAll()
}