package ru.iqsolution.tkoonline.local

import androidx.room.Room
import androidx.room.RoomDatabase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import ru.iqsolution.tkoonline.DB_NAME

val localModule = Kodein.Module("local") {

    bind<Preferences>() with provider {
        Preferences(instance())
    }

    bind<FileManager>() with provider {
        FileManager(instance())
    }

    bind<Database>() with singleton {
        Room.databaseBuilder(instance(), Database::class.java, DB_NAME)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .addMigrations(DummyMigration(8, 9))
            .addMigrations(Migration910())
            .build()
    }
}