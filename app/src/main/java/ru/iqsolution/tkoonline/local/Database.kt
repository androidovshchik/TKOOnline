package ru.iqsolution.tkoonline.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.iqsolution.tkoonline.local.dao.*
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent

@Database(
    entities = [
        AccessToken::class,
        PhotoEvent::class,
        CleanEvent::class,
        LocationEvent::class
    ],
    version = 9
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun baseDao(): BaseDao

    abstract fun tokenDao(): TokenDao

    abstract fun photoDao(): PhotoDao

    abstract fun cleanDao(): CleanDao

    abstract fun locationDao(): LocationDao
}