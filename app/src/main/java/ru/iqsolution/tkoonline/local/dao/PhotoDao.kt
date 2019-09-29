package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEventToken

@Dao
interface PhotoDao {

    @Query(
        """
        SELECT photo_events.*, tokens.* FROM photo_events 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id
    """
    )
    fun getEvents(): List<PhotoEventToken>

    @Insert
    fun insert(item: PhotoEvent)
}