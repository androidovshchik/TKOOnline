package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.CleanEventToken

@Dao
abstract class CleanDao {

    @Query(
        """
        SELECT * FROM clean_events 
        WHERE ce_related_id IS NULL AND ce_when_time LIKE :day || '%'
        ORDER BY ce_id DESC
    """
    )
    abstract fun getDayEvents(day: String): List<CleanEvent>

    @Query(
        """
        SELECT * FROM clean_events 
        WHERE ce_kp_id = :id AND ce_related_id IS NULL AND ce_when_time LIKE :day || '%'
        ORDER BY ce_id DESC
        LIMIT 1
    """
    )
    abstract fun getDayKpIdEvent(day: String, id: Int): CleanEvent?

    @Query(
        """
        SELECT clean_events.*, tokens.* FROM clean_events 
        INNER JOIN tokens ON clean_events.ce_token_id = tokens.t_id
        WHERE clean_events.ce_sent = 0
    """
    )
    abstract fun getSendEvents(): List<CleanEventToken>

    @Query(
        """
        SELECT clean_events.*, tokens.* FROM clean_events 
        INNER JOIN tokens ON clean_events.ce_token_id = tokens.t_id
        WHERE (clean_events.ce_kp_id = :id OR clean_events.ce_linked_id = :id) AND clean_events.ce_sent = 0
    """
    )
    abstract fun getSendKpIdEvents(id: Int): List<CleanEventToken>

    @Insert
    abstract fun insert(items: List<CleanEvent>)

    @Query(
        """
        UPDATE clean_events SET ce_sent = 1 WHERE ce_id = :id
    """
    )
    abstract fun markAsSent(id: Long)

    @Update
    abstract fun updateSimple(item: CleanEvent)

    @Query(
        """
        UPDATE clean_events SET ce_token_id = :token, ce_container_type = :latitude, ce_container_volume = :longitude, ce_container_count = :time, ce_when_time = :time
        WHERE ce_related_id = :id AND ce_sent = 0
    """
    )
    abstract fun updateRelated(id: Long, token: Long, latitude: Double, longitude: Double, time: String)

    /**
     * NOTICE some of them may be sent or not
     */
    @Transaction
    fun updateMultiple(item: CleanEvent) {
        require(item.id != null && item.kpId != null)
        if (!item.sent) {
            updateSimple(item)
        }
        updateRelated(
            item.id ?: 0L,
            item.tokenId,
            item.latitude,
            item.longitude,
            item.whenTime.toString(PATTERN_DATETIME)
        )
    }
}