package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Query
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
}