package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.models.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM tokens")
    fun getAllTokens(): List<Token>

    @Transaction
    @Insert
    fun insert(vararg items: Token)

    @Transaction
    @Delete
    fun delete(vararg items: Token)
}