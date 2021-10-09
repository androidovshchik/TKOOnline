package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Contact

@Dao
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
    abstract fun insertAll(items: List<Contact>)

    @Transaction
    open suspend fun safeInsert(car: Int, items: List<Contact>) {
        deleteAll(getAll(car).map { it.uid })
        insertAll(items)
    }

    @Query(
        """
        DELETE FROM contacts
        WHERE c_uid in (:uids)
    """
    )
    abstract fun deleteAll(uids: List<Long?>)
}