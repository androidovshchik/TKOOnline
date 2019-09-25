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
    fun getAllTokens(): List<AccessToken>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: AccessToken)

    @Delete
    fun delete(items: List<AccessToken>)
}