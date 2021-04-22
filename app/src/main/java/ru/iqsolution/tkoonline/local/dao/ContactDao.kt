package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Contact

@Dao
interface ContactDao {

    @Query(
        """
        SELECT * FROM contacts 
        ORDER BY c_name ASC
    """
    )
    fun getAll(): List<Contact>

    @Query(
        """
        SELECT * FROM contacts 
        WHERE c_phone = :numbers
        LIMIT 1
    """
    )
    fun getByPhone(numbers: String?): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Contact>)

    @Transaction
    fun safeInsert(items: List<Contact>) {
        deleteAll()
        insertAll(items)
    }

    @Query(
        """
        DELETE FROM contacts
    """
    )
    fun deleteAll()
}