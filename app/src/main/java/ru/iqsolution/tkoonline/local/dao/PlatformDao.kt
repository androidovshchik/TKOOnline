package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.Platform

@Dao
interface PlatformDao {

    @Query(
        """
        SELECT * FROM platforms 
        WHERE p_kp_id in (:kpIds)
        ORDER BY p_kp_id ASC
    """
    )
    fun getFromIds(kpIds: List<Long>): List<Platform>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: List<Platform>)

    @Query(
        """
        DELETE FROM platforms
    """
    )
    fun deleteAll()
}