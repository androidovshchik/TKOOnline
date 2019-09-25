package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.AccessToken

@Dao
interface TokenDao {

    @Query(
        """
        SELECT * FROM tokens
    """
    )
    fun getAllTokens(): List<AccessToken>

    @Insert
    fun insert(item: AccessToken)

    @Delete
    fun delete(items: List<AccessToken>)
}