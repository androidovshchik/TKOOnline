package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.TaskType

@Dao
abstract class TaskTypeDao {

    @Query(
        """
        SELECT task_types.* FROM task_types 
        INNER JOIN tokens ON tt_token_id = t_id
        WHERE t_car_id = :car
        ORDER BY tt_id ASC
    """
    )
    abstract suspend fun getAll(car: Int): List<TaskType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<TaskType>)

    @Transaction
    open suspend fun safeInsert(car: Int, items: List<TaskType>) {
        deleteAll(getAll(car).map { it.uid })
        insertAll(items)
    }

    @Query(
        """
        DELETE FROM task_types
        WHERE tt_uid in (:uids)
    """
    )
    abstract fun deleteAll(uids: List<Long?>)
}