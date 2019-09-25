package ru.iqsolution.tkoonline.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.iqsolution.tkoonline.local.dao.*
import ru.iqsolution.tkoonline.local.entities.*

@Database(
    entities = [
        AccessToken::class,
        Platform::class,
        PhotoType::class,
        PhotoEvent::class,
        CleanEvent::class,
        LocationEvent::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

    abstract fun platformDao(): PlatformDao

    abstract fun photoDao(): PhotoDao

    abstract fun cleanDao(): CleanDao

    abstract fun locationDao(): LocationDao
}