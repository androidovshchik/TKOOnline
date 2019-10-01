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
        ORDER BY clean_events.ce_id DESC
    """
    )
    fun getEvents(): List<CleanEventToken>

    @Query(
        """
        UPDATE clean_events SET ce_sent = 1 WHERE ce_id = :id
    """
    )
    fun markAsSent(id: Long)

    @Insert
    fun insert(item: CleanEvent)
}