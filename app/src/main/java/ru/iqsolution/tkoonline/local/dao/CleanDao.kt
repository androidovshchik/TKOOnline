package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.CleanEventRelated
import ru.iqsolution.tkoonline.local.entities.CleanEventToken

@Dao
abstract class CleanDao {

    @Query(
        """
        SELECT COUNT(*) FROM clean_events
        WHERE ce_sent = 0
    """
    )
    abstract fun getSendCount(): Int

    @Query(
        """
        SELECT * FROM clean_events 
        WHERE ce_related_id IS NULL AND ce_when_time LIKE :day || '%'
        ORDER BY ce_id DESC
    """
    )
    abstract fun getDayEvents(day: String): List<CleanEvent>

    @Transaction
    @Query(
        """
        SELECT * FROM clean_events 
        WHERE ce_kp_id = :kpId AND ce_related_id IS NULL AND ce_when_time LIKE :day || '%'
        ORDER BY ce_id DESC
        LIMIT 1
    """
    )
    abstract fun getDayKpEvent(day: String, kpId: Int): CleanEventRelated?

    @Query(
        """
        SELECT clean_events.*, tokens.* FROM clean_events 
        INNER JOIN tokens ON ce_token_id = t_id
        WHERE ce_sent = 0
        ORDER BY ce_id ASC
    """
    )
    abstract fun getSendEvents(): List<CleanEventToken>

    @Insert
    abstract fun insert(item: CleanEvent): Long

    @Insert
    abstract fun insertAll(item: List<CleanEvent>)

    /**
     * Normally one of events should be valid
     * @return list of kp ids of inserted events
     */
    @Transaction
    open fun insertMultiple(day: String, events: List<CleanEvent>): List<Int> {
        require(events.isNotEmpty())
        val primaryEvent = events[0]
        if (primaryEvent.isInvalid) {
            // it is important to have related id, so simply not sending this event
            primaryEvent.sent = true
        }
        val relatedId = insert(primaryEvent)
        val insertedEvents = mutableListOf(primaryEvent.kpId)
        val relatedEvents = events.drop(1)
            .filter { !it.isInvalid }
        if (relatedEvents.isNotEmpty()) {
            insertAll(relatedEvents.apply {
                forEach {
                    it.relatedId = relatedId
                    insertedEvents.add(it.kpId)
                }
            })
        }
        return insertedEvents
    }

    @Query(
        """
        UPDATE clean_events 
        SET ce_sent = 1 
        WHERE ce_id = :id
    """
    )
    abstract fun markAsSent(id: Long)
}