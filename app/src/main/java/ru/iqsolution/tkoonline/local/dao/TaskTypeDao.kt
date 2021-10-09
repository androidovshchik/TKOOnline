package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.TaskType

@Dao
@Suppress("FunctionName")
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
    abstract fun _insertAll(items: List<TaskType>)

    @Transaction
    open suspend fun replaceAll(car: Int, items: List<TaskType>) {
        _deleteAll(getAll(car).map { it.uid })
        _insertAll(items)
    }

    @Query(
        """
        DELETE FROM task_types
        WHERE tt_uid in (:uids)
    """
    )
    abstract fun _deleteAll(uids: List<Long?>)
}