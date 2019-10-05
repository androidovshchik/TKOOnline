package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEventToken

@Dao
abstract class PhotoDao {

    @Query(
        """
        SELECT * FROM photo_events
        WHERE pe_related_id IS NULL AND pe_when_time LIKE :day || '%'
        ORDER BY pe_id DESC
    """
    )
    abstract fun getDayEvents(day: String): List<PhotoEvent>

    @Query(
        """
        SELECT * FROM photo_events 
        WHERE pe_kp_id = :id AND pe_related_id IS NULL AND pe_when_time LIKE :day || '%'
    """
    )
    abstract fun getDayKpIdEvents(day: String, id: Int): List<PhotoEvent>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
        WHERE photo_events.pe_sent = 0
    """
    )
    abstract fun getSendEvents(): List<PhotoEventToken>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
        WHERE (photo_events.pe_kp_id = :id OR photo_events.pe_linked_id = :id) AND photo_events.pe_sent = 0
    """
    )
    abstract fun getSendKpIdEvents(id: Int): List<PhotoEventToken>

    @Insert
    abstract fun insert(item: PhotoEvent): Long

    @Transaction
    fun insertMultiple(item: PhotoEvent, linkedIds: List<Int>) {
        require(item.id == null && item.kpId != null)
        val related = insert(item)
        linkedIds.forEach {
            insert(item.apply {
                id = null
                linkedId = it
                relatedId = related
            })
        }
        // reset values
        item.apply {
            id = null
            linkedId = null
            relatedId = null
        }
    }

    @Update
    abstract fun updateSimple(item: PhotoEvent)

    @Query(
        """
        UPDATE photo_events SET pe_token_id = :token, pe_latitude = :latitude, pe_longitude = :longitude, pe_when_time = :time 
        WHERE pe_related_id = :id AND pe_sent = 0
    """
    )
    abstract fun updateRelated(id: Long, token: Long, latitude: Double, longitude: Double, time: String)

    /**
     * NOTICE some of them may be sent or not
     */
    @Transaction
    fun updateMultiple(item: PhotoEvent) {
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

    @Query(
        """
        UPDATE photo_events SET pe_sent = 1 WHERE pe_id = :id
    """
    )
    abstract fun markAsSent(id: Long)

    @Delete
    abstract fun delete(item: PhotoEvent)
}