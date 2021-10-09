package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.PhotoType

@Dao
@Suppress("FunctionName")
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
    abstract fun _insertAll(items: List<PhotoType>)

    @Transaction
    open suspend fun replaceAll(car: Int, items: List<PhotoType>) {
        _deleteAll(getAll(car).map { it.uid })
        _insertAll(items)
    }

    @Query(
        """
        DELETE FROM photo_types
        WHERE pt_uid in (:uids)
    """
    )
    abstract fun _deleteAll(uids: List<Long?>)
}