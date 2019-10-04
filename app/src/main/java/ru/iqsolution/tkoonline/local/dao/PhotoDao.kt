package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEventToken

@Dao
interface PhotoDao {

    @Query(
        """
        SELECT * FROM photo_events
        WHERE pe_when_time LIKE :day || '%'
        ORDER BY pe_id DESC
    """
    )
    fun getDayEvents(day: String): List<PhotoEvent>

    @Query(
        """
        SELECT * FROM photo_events 
        WHERE pe_kp_id = :id AND pe_when_time LIKE :day || '%'
    """
    )
    fun getDayKpIdEvents(day: String, id: Int): List<PhotoEvent>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
        WHERE photo_events.pe_sent = 0
    """
    )
    fun getSendEvents(): List<PhotoEventToken>

    @Query(
        """
        UPDATE photo_events SET pe_sent = 1 WHERE pe_id = :id
    """
    )
    fun markAsSent(id: Long)

    @Insert
    fun insert(item: PhotoEvent)

    @Delete
    fun delete(item: PhotoEvent)
}