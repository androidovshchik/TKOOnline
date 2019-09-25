package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoType

/**
 * Includes also photo types table
 */
@Dao
interface PhotoDao {

    @Query("SELECT photo_types.* FROM photo_types INNER JOIN tokens ON photo_types.token_id = tokens.id WHERE tokens.token = :token")
    fun getAllEvents(token: String): List<PhotoEvent>

    @Query("SELECT photo_types.* FROM photo_types INNER JOIN tokens ON photo_types.pt_token_id = tokens.t_id WHERE tokens.t_value = :token")
    fun getAllTypes(token: String): List<PhotoType>

    @Insert
    fun insertEvent(item: PhotoEvent)

    @Insert
    fun insertTypes(items: List<PhotoType>)
}