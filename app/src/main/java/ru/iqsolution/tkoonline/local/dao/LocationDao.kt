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
        SELECT location_events.*, tokens.* FROM location_events 
        INNER JOIN tokens ON location_events.le_token_id = tokens.t_id
    """
    )
    fun getEvents(): List<LocationEventToken>

    @Query(
        """
        UPDATE location_events SET le_sent = 1 WHERE le_id = :id
    """
    )
    fun markAsSent(id: Long)

    @Insert
    fun insert(item: LocationEvent)

    /**
     * Lifetime is less than 48 hours
     */
    @Delete
    fun delete(items: List<LocationEvent>)
}