package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.TagEvent

@Dao
interface TagDao {

    @Query(
        """
        SELECT * FROM tag_events 
        WHERE te_kp_id = :kpId AND te_when_time LIKE :day || '%'
        ORDER BY te_id ASC
    """
    )
    fun getDayKpEvents(day: String, kpId: Int): List<TagEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TagEvent)
}