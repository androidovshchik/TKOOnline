package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.Platform

@Dao
interface PlatformDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: List<Platform>)

    @Query(
        """
        DELETE FROM platforms
    """
    )
    fun deleteAll()
}