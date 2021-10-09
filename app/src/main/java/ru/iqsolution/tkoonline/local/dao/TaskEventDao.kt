package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.iqsolution.tkoonline.local.entities.TaskEvent
import ru.iqsolution.tkoonline.local.entities.TaskEventToken

@Dao
abstract class TaskEventDao {

    @Query(
        """
        SELECT COUNT(*) FROM task_events
        WHERE te_sent = 0
    """
    )
    abstract fun getSendCount(): Int

    @Query(
        """
        SELECT * FROM task_events 
        INNER JOIN tokens ON te_token_id = t_id
        WHERE t_car_id = :car AND te_route_id = :route AND te_when_time LIKE :day || '%'
        ORDER BY te_id DESC
    """
    )
    abstract fun getDayEvents(car: Int, route: String?, day: String): List<TaskEvent>

    @Transaction
    @Query(
        """
        SELECT * FROM task_events 
        INNER JOIN tokens ON te_token_id = t_id
        WHERE t_car_id = :car AND te_route_id = :route AND te_task_id = :taskId AND te_when_time LIKE :day || '%'
        ORDER BY te_id DESC
        LIMIT 1
    """
    )
    abstract fun getLastEvent(car: Int, route: String?, taskId: Int, day: String): TaskEvent?

    @Query(
        """
        SELECT task_events.*, tokens.* FROM task_events 
        INNER JOIN tokens ON te_token_id = t_id
        WHERE te_sent = 0
        ORDER BY te_id ASC
    """
    )
    abstract fun getSendEvents(): List<TaskEventToken>

    @Insert
    abstract fun insert(item: TaskEvent): Long

    @Query(
        """
        UPDATE task_events 
        SET te_sent = 1 
        WHERE te_id = :id
    """
    )
    abstract fun markAsSent(id: Long)
}