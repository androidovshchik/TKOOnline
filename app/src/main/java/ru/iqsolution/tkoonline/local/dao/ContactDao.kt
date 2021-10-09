package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Contact

@Dao
@Suppress("FunctionName")
abstract class ContactDao {

    @Query(
        """
        SELECT * FROM contacts 
        INNER JOIN tokens ON c_token_id = t_id
        WHERE t_car_id = :car
        ORDER BY c_name ASC
    """
    )
    abstract suspend fun getAll(car: Int): List<Contact>

    @Query(
        """
        SELECT * FROM contacts 
        INNER JOIN tokens ON c_token_id = t_id
        WHERE t_car_id = :car AND c_phone = :numbers
        LIMIT 1
    """
    )
    abstract suspend fun getByPhone(car: Int, numbers: String?): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insertAll(items: List<Contact>)

    @Transaction
    open suspend fun replaceAll(car: Int, items: List<Contact>) {
        _deleteAll(getAll(car).map { it.uid })
        _insertAll(items)
    }

    @Query(
        """
        DELETE FROM contacts
        WHERE c_uid in (:uids)
    """
    )
    abstract fun _deleteAll(uids: List<Long?>)
}