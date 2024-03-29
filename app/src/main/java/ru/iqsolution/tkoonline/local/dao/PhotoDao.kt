package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.extensions.PATTERN_DATETIME_ZONE
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
        ORDER BY pe_id ASC
    """
    )
    abstract fun getDayKpEvents(day: String, kpId: Int): List<PhotoEvent>

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON pe_token_id = t_id
        WHERE pe_ready = 1 AND pe_sent = 0
    """
    )
    abstract fun getSendEvents(): List<PhotoEventToken>

    @Insert
    abstract fun insert(item: PhotoEvent): Long

    @Transaction
    open fun insertMultiple(item: PhotoEvent, linkedIds: List<Int>) {
        require(item.id == null && item.kpId != null)
        val kpId = item.kpId
        val relatedId = insert(item)
        linkedIds.forEach { linkedId ->
            insert(item.also {
                it.id = null
                it.kpId = linkedId
                it.linkedId = kpId
                it.relatedId = relatedId
            })
        }
    }

    @Update
    abstract fun update(item: PhotoEvent)

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
            update(item)
        }
        updateRelated(
            item.id ?: 0L,
            item.tokenId,
            item.latitude,
            item.longitude,
            item.whenTime.toString(PATTERN_DATETIME_ZONE)
        )
    }

    @Query(
        """
        UPDATE photo_events
        SET pe_ready = :ready
        WHERE pe_kp_id in (:kpIds) AND pe_when_time LIKE :day || '%'
    """
    )
    abstract fun markReady(day: String, kpIds: List<Int>, ready: Int)

    @Transaction
    open fun markReadyMultiple(day: String, allKpIds: List<Int>, validKpIds: List<Int>) {
        require(allKpIds.isNotEmpty())
        markReady(day, allKpIds, 0)
        if (validKpIds.isNotEmpty()) {
            markReady(day, validKpIds, 1)
        }
    }

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