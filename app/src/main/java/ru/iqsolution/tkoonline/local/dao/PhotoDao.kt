package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.PATTERN_DATETIME
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
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
        WHERE photo_events.pe_kp_id = :id AND photo_events.pe_sent = 0
    """
    )
    fun getSendKpIdEvents(id: Int): List<PhotoEventToken>

    @Query(
        """
        UPDATE photo_events SET pe_sent = 1 WHERE pe_id = :id
    """
    )
    fun markAsSent(id: Long)

    @Insert
    fun insert(item: PhotoEvent): Long

    @Transaction
    fun insertMultiple(item: PhotoEvent, linkedIds: List<Int>) {
        val kp = item.kpId
        val related = insert(item)
        linkedIds.forEach {
            insert(item.apply {
                id = null
                kpId = it
                relatedId = related
            })
        }
        // reset values
        item.apply {
            id = null
            kpId = kp
            relatedId = null
        }
    }

    @Update
    fun updateSimple(item: PhotoEvent)

    @Query(
        """
        UPDATE photo_events SET pe_token_id = :token, pe_latitude = :latitude, pe_longitude = :longitude, pe_when_time = :time 
        WHERE pe_related_id =:id AND pe_sent = 0
    """
    )
    fun updateRelated(id: Long, token: Long, latitude: Double, longitude: Double, time: String)

    @Transaction
    fun updateMultiple(item: PhotoEvent) {
        updateSimple(item)
        updateRelated(
            item.id ?: 0L,
            item.tokenId,
            item.latitude,
            item.longitude,
            item.whenTime.toString(PATTERN_DATETIME)
        )
    }

    @Delete
    fun delete(item: PhotoEvent)
}