package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.local.entities.PlatformContainersEvents

@Dao
interface PlatformDao {

    /**
     * For platforms screen
     */
    @Transaction
    @Query(
        """
        SELECT platforms.* FROM platforms 
        INNER JOIN tokens ON platforms.pf_token_id = tokens.t_id 
        WHERE tokens.t_token = :token
    """
    )
    fun getPlatformsByToken(token: String): List<PlatformContainersEvents>

    /**
     * For platforms screen
     */
    @Transaction
    @Query(
        """
        SELECT platforms.* FROM platforms 
        INNER JOIN tokens ON platforms.pf_token_id = tokens.t_id 
        WHERE tokens.t_token = :token
    """
    )
    fun getPlatformById(id: Long): List<PlatformContainersEvents>

    @Insert
    fun insert(items: List<Platform>)
}