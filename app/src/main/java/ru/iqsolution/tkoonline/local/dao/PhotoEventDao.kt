package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEventToken
import ru.iqsolution.tkoonline.local.entities.TaskEvent

@Dao
@Suppress("FunctionName")
abstract class PhotoEventDao {

    @Query(
        """
        SELECT COUNT(*) FROM photo_events
        WHERE pe_event_id IS NOT NULL AND pe_sent = 0
    """
    )
    abstract suspend fun getSendCount(): Int

    @Query(
        """
        SELECT * FROM photo_events
        INNER JOIN tokens ON pe_token_id = t_id
        WHERE t_car_id = :car AND pe_route_id = :route AND pe_when_time LIKE :day || '%'
        ORDER BY pe_id DESC
    """
    )
    abstract suspend fun getDayEvents(car: Int, route: String?, day: String): List<PhotoEvent>

    @Query(
        """
        SELECT * FROM photo_events 
        INNER JOIN tokens ON pe_token_id = t_id
        WHERE t_car_id = :car AND pe_route_id = :route 
            AND (pe_task_uid = :taskUid OR (pe_task_id IS NOT NULL AND pe_task_id = :taskId)) 
            AND pe_when_time LIKE :day || '%'
        ORDER BY pe_id ASC
    """
    )
    abstract suspend fun getTaskEvents(car: Int, route: String?, taskUid: Long?, taskId: Int?, day: String): List<PhotoEvent>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON pe_token_id = t_id
        WHERE pe_event_id IS NOT NULL AND pe_sent = 0
        ORDER BY pe_id ASC
    """
    )
    abstract suspend fun getSendEvents(): List<PhotoEventToken>

    @Insert
    abstract suspend fun insert(item: PhotoEvent)

    @Update
    abstract suspend fun update(item: PhotoEvent)

    @Query(
        """
        UPDATE photo_events
        SET pe_event_id = :eventId
        WHERE pe_id in (:ids)
    """
    )
    abstract fun _updateEvent(ids: List<Long?>, eventId: Int)

    @Transaction
    open suspend fun changeEvent(car: Int, route: String?, event: TaskEvent, eventId: Int, day: String) {
        val events = getTaskEvents(car, route, event.taskUid, event.taskId, day)
        _updateEvent(events.filter { it.eventId == null }.map { it.id }, eventId)
    }

    @Query(
        """
        UPDATE photo_events 
        SET pe_task_id = :taskId
        WHERE pe_task_uid = :taskUid
    """
    )
    abstract suspend fun updateId(taskUid: Long?, taskId: Int?)

    @Query(
        """
        UPDATE photo_events
        SET pe_sent = 1
        WHERE pe_id = :id
    """
    )
    abstract suspend fun markAsSent(id: Long)

    @Delete
    abstract suspend fun delete(item: PhotoEvent)
}