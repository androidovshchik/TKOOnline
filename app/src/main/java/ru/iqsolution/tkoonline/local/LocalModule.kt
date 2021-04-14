package ru.iqsolution.tkoonline.local

import androidx.room.Room
import androidx.room.RoomDatabase
import org.kodein.di.*
import ru.iqsolution.tkoonline.DB_NAME

val localModule = DI.Module("local") {

    bind<Preferences>() with provider {
        Preferences(instance())
    }

    bind<FileManager>() with provider {
        FileManager(instance())
    }

    bind<Database>() with singleton {
        Room.databaseBuilder(instance(), Database::class.java, DB_NAME)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .addMigrations(Migration910())
            .addMigrations(Migration1011())
            .build()
    }
}