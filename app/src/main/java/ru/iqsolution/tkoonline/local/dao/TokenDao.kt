package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.iqsolution.tkoonline.local.models.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM tokens")
    fun getAllTokens(): List<Token>

    @Insert
    fun insert(item: Token)

    @Delete
    fun delete(vararg items: Token)
}