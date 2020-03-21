package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Platform

@Dao
abstract class PlatformDao {

    @Query(
        """
        SELECT * FROM platforms 
        WHERE p_kp_id in (:kpIds)
        ORDER BY p_kp_id ASC
    """
    )
    abstract fun getFromIds(kpIds: List<Int>): List<Platform>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<Platform>)

    @Transaction
    open fun safeInsert(items: List<Platform>) {
        deleteAll()
        insertAll(items)
    }

    @Query(
        """
        DELETE FROM platforms
    """
    )
    abstract fun deleteAll()
}