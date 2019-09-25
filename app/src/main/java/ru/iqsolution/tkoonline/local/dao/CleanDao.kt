package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.CleanEventToken

@Dao
interface CleanDao {

    @Query(
        """
        SELECT clean_events.*, tokens.* FROM clean_events 
        INNER JOIN tokens ON clean_events.ce_token_id = tokens.t_id 
        WHERE clean_events.ce_sent = 0
    """
    )
    fun getEventsForSend(): List<CleanEventToken>

    @Insert
    fun insert(item: CleanEvent)
}