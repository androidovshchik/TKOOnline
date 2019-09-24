package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.models.PhotoEvent
import ru.iqsolution.tkoonline.local.models.PhotoType

/**
 * Includes also photo types table
 */
@Dao
interface PhotoDao {

    @Query("SELECT photo_types.* FROM photo_types INNER JOIN tokens ON photo_types.token_id = tokens.id WHERE tokens.token = :token")
    fun getAllEvents(token: String): List<PhotoEvent>

    @Query("SELECT photo_types.* FROM photo_types INNER JOIN tokens ON photo_types.token_id = tokens.id WHERE tokens.token = :token")
    fun getAllTypes(token: String): List<PhotoType>

    @Insert
    fun insertEvent(item: PhotoEvent)

    @Insert
    fun insertTypes(vararg items: PhotoType)
}