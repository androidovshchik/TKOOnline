package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Task

@Dao
abstract class TaskDao {

    @Query(
        """
        SELECT tasks.* FROM tasks 
        INNER JOIN tokens ON tk_token_id = t_id
        WHERE t_car_id = :car AND tk_route_id = :route AND tk_when_day LIKE :day || '%'
    """
    )
    abstract suspend fun getByRouteDay(car: Int, route: String?, day: String): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<Task>)

    @Transaction
    open suspend fun safeInsert(car: Int, route: String?, day: String, items: List<Task>) {
        deleteAll(getByRouteDay(car, route, day).filter { !it.draft }.map { it.uid })
        insertAll(items)
    }

    @Query(
        """
        UPDATE tasks 
        SET tk_status = :status
        WHERE tk_uid = :uid
    """
    )
    abstract suspend fun updateStatus(uid: Long?, status: Int)

    @Query(
        """
        UPDATE tasks 
        SET tk_id = :id
        WHERE tk_uid = :uid
    """
    )
    abstract suspend fun updateId(uid: Long?, id: Int?)

    @Query(
        """
        DELETE FROM tasks
        WHERE tk_uid in (:uids)
    """
    )
    abstract fun deleteAll(uids: List<Long?>)
}