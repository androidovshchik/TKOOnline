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
        WHERE location_events.le_sent = 0
    """
    )
    fun getEventsForSend(): List<LocationEventToken>

    @Insert
    fun insert(item: LocationEvent)

    @Delete
    fun delete(items: List<LocationEvent>)
}