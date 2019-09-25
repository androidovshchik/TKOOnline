package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEventTypeToken
import ru.iqsolution.tkoonline.local.entities.PhotoType

/**
 * Includes also photo types table
 */
@Dao
interface PhotoDao {

    /**
     * For problem screen
     */
    @Query(
        """
        SELECT photo_types.* FROM photo_types 
        INNER JOIN tokens ON photo_types.pt_token_id = tokens.t_id 
        WHERE tokens.t_token = :token
    """
    )
    fun getTypesByToken(token: String): List<PhotoType>

    /**
     * For send
     */
    @Query(
        """
        SELECT photo_events.*, photo_types.*, tokens.* FROM photo_events 
        INNER JOIN photo_types ON photo_events.pe_type_id = photo_types.pt_id 
        INNER JOIN tokens ON photo_events.pe_token_id = tokens.t_id 
        WHERE photo_events.pe_sent = 0 AND tokens.t_token = :token
    """
    )
    fun getEventsByToken(token: String): List<PhotoEventTypeToken>

    @Insert
    fun insertTypes(items: List<PhotoType>)

    @Insert
    fun insertEvent(item: PhotoEvent)
}