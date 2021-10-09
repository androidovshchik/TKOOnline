package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.PhotoType

@Dao
abstract class PhotoTypeDao {

    @Query(
        """
        SELECT photo_types.* FROM photo_types 
        INNER JOIN tokens ON pt_token_id = t_id
        WHERE t_car_id = :car
        ORDER BY pt_id ASC
    """
    )
    abstract suspend fun getAll(car: Int): List<PhotoType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<PhotoType>)

    @Transaction
    open suspend fun safeInsert(car: Int, items: List<PhotoType>) {
        deleteAll(getAll(car).map { it.uid })
        insertAll(items)
    }

    @Query(
        """
        DELETE FROM photo_types
        WHERE pt_uid in (:uids)
    """
    )
    abstract fun deleteAll(uids: List<Long?>)
}