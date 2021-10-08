package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
abstract class BaseDao {

    @RawQuery
    abstract suspend fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}