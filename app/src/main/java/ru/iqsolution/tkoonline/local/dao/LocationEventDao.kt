package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.local.entities.LocationEventToken

@Dao
abstract class LocationEventDao {

    @Query(
        """
        SELECT COUNT(*) FROM location_events
        WHERE le_sent = 0
    """
    )
    abstract suspend fun getSendCount(): Int

    /**
     * Only debug feature for routing
     */
    @Query(
        """
        SELECT location_events.*, tokens.* FROM location_events 
        INNER JOIN tokens ON le_token_id = t_id
        WHERE t_car_id = :car AND le_when_time LIKE :day || '%'
        ORDER BY le_id ASC
    """
    )
    abstract suspend fun getDayEvents(car: Int, day: String): List<LocationEventToken>

    @Query(
        """
        SELECT location_events.*, tokens.* FROM location_events 
        INNER JOIN tokens ON le_token_id = t_id
        WHERE le_sent = 0
        ORDER BY le_id DESC
        LIMIT 1
    """
    )
    abstract suspend fun getLastEvent(): LocationEventToken?

    @Insert
    abstract suspend fun insert(item: LocationEvent)

    @Query(
        """
        UPDATE location_events 
        SET le_sent = 1 
        WHERE le_id = :id
    """
    )
    abstract suspend fun markAsSent(id: Long)

    /**
     * Lifetime is less than 48 hours
     */
    @Delete
    abstract suspend fun delete(item: LocationEvent)
}