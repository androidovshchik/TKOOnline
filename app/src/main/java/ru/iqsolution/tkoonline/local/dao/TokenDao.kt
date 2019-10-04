package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.AccessToken

@Dao
interface TokenDao {

    @Query(
        """
        SELECT * FROM tokens
    """
    )
    fun getTokens(): List<AccessToken>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: AccessToken): Long

    @Delete
    fun delete(items: List<AccessToken>)
}