package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Token

@Dao
abstract class TokenDao {

    @Query(
        """
        SELECT * FROM tokens
        ORDER BY t_id ASC
    """
    )
    abstract fun getAll(): List<Token>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(item: Token): Long

    @Delete
    abstract fun delete(items: List<Token>)
}