package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Platform

@Dao
abstract class PlatformDao {

    @Query(
        """
        SELECT * FROM platforms 
        ORDER BY p_kp_id ASC
    """
    )
    abstract fun getAll(): List<Platform>

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
        UPDATE platforms 
        SET p_status = :status
        WHERE p_kp_id = :kpId
    """
    )
    abstract fun updateStatus(kpId: Int, status: Int)

    @Query(
        """
        DELETE FROM platforms
    """
    )
    abstract fun deleteAll()
}