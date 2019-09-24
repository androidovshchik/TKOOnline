package ru.iqsolution.tkoonline.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.iqsolution.tkoonline.local.dao.CleanDao
import ru.iqsolution.tkoonline.local.models.*

@Database(
    entities = [
        AccessToken::class,
        Platform::class,
        PhotoType::class,
        CleanEvent::class,
        LocationEvent::class,
        LocationEvent::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cleanDao(): CleanDao

    abstract fun eventDao(): CleanDao

    abstract fun eventDao(): CleanDao

    abstract fun eventDao(): CleanDao
}