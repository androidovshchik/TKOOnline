package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEventToken

@Dao
abstract class PhotoDao {

    @Query(
        """
        SELECT COUNT(*) FROM photo_events
        WHERE pe_ready = 1 AND pe_sent = 0
    """
    )
    abstract fun getSendCount(): Int

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
        WHERE pe_kp_id = :kpId AND pe_related_id IS NULL AND pe_when_time LIKE :day || '%'
    """
    )
    abstract fun getDayKpIdEvents(day: String, kpId: Int): List<PhotoEvent>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
        WHERE photo_events.pe_ready = 1 AND photo_events.pe_sent = 0
    """
    )
    abstract fun getSendEvents(): List<PhotoEventToken>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
        WHERE photo_events.pe_kp_id IS NULL AND photo_events.pe_sent = 0
    """
    )
    abstract fun getSendNoKpEvents(): List<PhotoEventToken>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
        WHERE (photo_events.pe_kp_id = :kpId OR photo_events.pe_linked_id = :kpId) AND photo_events.pe_sent = 0
    """
    )
    abstract fun getSendKpIdEvents(kpId: Int): List<PhotoEventToken>

    @Insert
    abstract fun insert(item: PhotoEvent): Long

    @Transaction
    open fun insertMultiple(item: PhotoEvent, linkedIds: List<Int>) {
        require(item.id == null && item.kpId != null)
        val kp = item.kpId
        val related = insert(item)
        linkedIds.forEach {
            insert(item.apply {
                id = null
                kpId = it
                linkedId = kp
                relatedId = related
            })
        }
        // apply changes
        item.apply {
            id = related
            kpId = kp
            linkedId = null
            relatedId = null
        }
    }

    @Update
    abstract fun updateSimple(item: PhotoEvent)

    @Query(
        """
        UPDATE photo_events 
        SET pe_token_id = :token, pe_latitude = :latitude, pe_longitude = :longitude, pe_when_time = :time 
        WHERE pe_related_id = :relatedId AND pe_sent = 0
    """
    )
    abstract fun updateRelated(relatedId: Long, token: Long, latitude: Double, longitude: Double, time: String)

    /**
     * NOTICE some of them may be sent or not
     */
    @Transaction
    open fun updateMultiple(item: PhotoEvent) {
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
        UPDATE photo_events
        SET pe_ready = 1
        WHERE pe_kp_id = :kpId AND pe_when_time LIKE :day || '%'
    """
    )
    abstract fun markAsReady(day: String, kpId: Int)

    @Query(
        """
        UPDATE photo_events
        SET pe_sent = 1
        WHERE pe_id = :id
    """
    )
    abstract fun markAsSent(id: Long)

    @Delete
    abstract fun delete(item: PhotoEvent)
}