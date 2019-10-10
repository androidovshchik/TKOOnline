package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.local.entities.LocationEventToken

@Dao
interface LocationDao {

    @Query(
        """
        SELECT COUNT(*) FROM location_events
        WHERE le_sent = 0
    """
    )
    fun getSendCount(): Int

    @Query(
        """
        SELECT location_events.*, tokens.* FROM location_events 
        INNER JOIN tokens ON location_events.le_token_id = tokens.t_id
        ORDER BY location_events.le_id DESC
        LIMIT 3
    """
    )
    fun getSendEvents(): List<LocationEventToken>

    @Insert
    fun insert(item: LocationEvent)

    @Query(
        """
        UPDATE location_events SET le_sent = 1 WHERE le_id = :id
    """
    )
    fun markAsSent(id: Long)

    /**
     * Lifetime is less than 48 hours
     */
    @Delete
    fun delete(items: List<LocationEvent>)
}