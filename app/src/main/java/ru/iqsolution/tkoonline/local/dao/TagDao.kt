package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.iqsolution.tkoonline.local.entities.TagEvent

@Dao
interface TagDao {

    @Query(
        """
        SELECT * FROM tag_events 
        WHERE te_kp_id = :kpId AND te_when_time LIKE :day || '%'
        ORDER BY te_when_time ASC
    """
    )
    fun observeDayKpEvents(day: String, kpId: Int): Flow<List<TagEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TagEvent)
}