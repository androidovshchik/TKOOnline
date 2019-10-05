package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.CleanEventRelated
import ru.iqsolution.tkoonline.local.entities.CleanEventToken
import ru.iqsolution.tkoonline.models.SimpleContainer

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

    @Transaction
    @Query(
        """
        SELECT * FROM clean_events 
        WHERE ce_kp_id = :kpId AND ce_related_id IS NULL AND ce_when_time LIKE :day || '%'
        ORDER BY ce_id DESC
        LIMIT 1
    """
    )
    abstract fun getDayKpIdEvents(day: String, kpId: Int): CleanEventRelated?

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
        WHERE (clean_events.ce_kp_id = :kpId OR clean_events.ce_linked_id = :kpId) AND clean_events.ce_sent = 0
    """
    )
    abstract fun getSendKpIdEvents(kpId: Int): List<CleanEventToken>

    @Insert
    abstract fun insert(item: CleanEvent): Long

    @Transaction
    fun insertMultiple(day: String, item: CleanEvent, containers: List<SimpleContainer>) {
        val kp = item.kpId
        deleteDayKpId(day, kp)
        val related = insert(item)
        val container = item.toSimpleContainer()
        containers.forEach {
            item.setFromAny(it)
            it.linkedIds.forEach { linked ->
                insert(item.apply {
                    id = null
                    kpId = linked
                    linkedId = kp
                    relatedId = related
                })
            }
        }
        // apply changes
        item.apply {
            id = related
            kpId = kp
            linkedId = null
            relatedId = null
            setFromAny(container)
        }
    }

    @Query(
        """
        UPDATE clean_events 
        SET ce_sent = 1 
        WHERE ce_id = :id
    """
    )
    abstract fun markAsSent(id: Long)

    @Query(
        """
        DELETE FROM clean_events
        WHERE ce_kp_id = :kpId AND ce_related_id IS NULL AND ce_sent = 0 AND ce_when_time LIKE :day || '%'
    """
    )
    abstract fun deleteDayKpId(day: String, kpId: Int)
}