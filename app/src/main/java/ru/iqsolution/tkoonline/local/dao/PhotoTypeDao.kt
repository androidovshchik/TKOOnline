package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.PhotoType

@Dao
abstract class PhotoTypeDao {

    @Query(
        """
        SELECT * FROM photo_types 
        ORDER BY pt_id ASC
    """
    )
    abstract fun getAll(): List<PhotoType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<PhotoType>)

    @Transaction
    open fun safeInsert(items: List<PhotoType>) {
        deleteAll()
        insertAll(items)
    }

    @Query(
        """
        DELETE FROM photo_types
    """
    )
    abstract fun deleteAll()
}