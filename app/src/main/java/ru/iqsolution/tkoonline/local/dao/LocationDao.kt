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
        INNER JOIN tokens ON le_token_id = t_id
        WHERE le_sent = 0
        ORDER BY le_id DESC
        LIMIT 1
    """
    )
    fun getLastSendEvent(): LocationEventToken?

    /**
     * Only debug feature for routing
     */
    @Query(
        """
        SELECT location_events.*, tokens.* FROM location_events 
        INNER JOIN tokens ON le_token_id = t_id
        WHERE t_car_id = :carId AND le_when_time LIKE :day || '%'
        ORDER BY le_id ASC
    """
    )
    fun getDayCarEvents(day: String, carId: Int): List<LocationEventToken>

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
    fun delete(items: LocationEvent)
}