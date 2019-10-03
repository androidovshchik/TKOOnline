package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.CleanEventToken

@Dao
interface CleanDao {

    @Query(
        """
        SELECT * FROM clean_events 
        WHERE ce_when_time LIKE :day || '%'
        ORDER BY ce_id DESC
    """
    )
    fun getDayEvents(day: String): List<CleanEvent>

    @Query(
        """
        SELECT * FROM clean_events 
        WHERE ce_kp_id = :id AND ce_when_time LIKE :day || '%'
        ORDER BY ce_id DESC
        LIMIT 1
    """
    )
    fun getDayKpIdEvent(day: String, id: Int): CleanEvent?

    @Query(
        """
        SELECT clean_events.*, tokens.* FROM clean_events 
        INNER JOIN tokens ON clean_events.ce_token_id = tokens.t_id
        WHERE clean_events.ce_sent = 0
    """
    )
    fun getSendEvents(): List<CleanEventToken>

    @Query(
        """
        UPDATE clean_events SET ce_sent = 1 WHERE ce_id = :id
    """
    )
    fun markAsSent(id: Long)

    @Insert
    fun insert(item: CleanEvent)
}