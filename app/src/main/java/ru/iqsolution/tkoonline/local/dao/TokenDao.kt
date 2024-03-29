package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.AccessToken

@Dao
interface TokenDao {

    @Query(
        """
        SELECT * FROM tokens
        ORDER BY t_id ASC
    """
    )
    fun getAll(): List<AccessToken>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: AccessToken): Long

    @Delete
    fun delete(items: List<AccessToken>)
}