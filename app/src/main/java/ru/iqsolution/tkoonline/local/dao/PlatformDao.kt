package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.local.entities.PlatformContainersPhoto
import ru.iqsolution.tkoonline.local.entities.PlatformContainersPhotoClean

@Dao
interface PlatformDao {

    /**
     * For platforms screen
     */
    @Transaction
    @Query(
        """
        SELECT platforms.* FROM platforms 
        INNER JOIN tokens ON platforms.p_token_id = tokens.t_id 
        WHERE platforms.p_linked_id IS NULL AND tokens.t_token = :token
    """
    )
    fun getPlatformsByToken(token: String): List<PlatformContainersPhoto>

    /**
     * For platform screen
     */
    @Transaction
    @Query(
        """
        SELECT * FROM platforms 
        WHERE p_id = :id LIMIT 1
    """
    )
    fun getPlatformById(id: Long): PlatformContainersPhotoClean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Platform>)
}