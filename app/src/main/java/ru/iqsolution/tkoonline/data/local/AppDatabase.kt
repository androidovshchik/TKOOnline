package ru.iqsolution.tkoonline.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.iqsolution.tkoonline.data.models.CleanEvent

@Database(
    entities = [
        CleanEvent::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
}