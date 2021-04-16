package ru.iqsolution.tkoonline.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.iqsolution.tkoonline.local.dao.*
import ru.iqsolution.tkoonline.local.entities.*

@Database(
    entities = [
        AccessToken::class,
        PhotoEvent::class,
        CleanEvent::class,
        LocationEvent::class,
        Platform::class,
        PhotoType::class,
        Contact::class
    ],
    version = 12
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun baseDao(): BaseDao

    abstract fun tokenDao(): TokenDao

    abstract fun photoDao(): PhotoDao

    abstract fun cleanDao(): CleanDao

    abstract fun locationDao(): LocationDao

    abstract fun platformDao(): PlatformDao

    abstract fun typeDao(): TypeDao

    abstract fun contactDao(): ContactDao
}

class Migration910 : Migration(9, 10) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `platforms` (`p_kp_id` INTEGER NOT NULL, `p_linked_id` INTEGER, `p_address` TEXT NOT NULL, `p_lat` REAL NOT NULL, `p_lon` REAL NOT NULL, `p_bal_keeper` TEXT, `p_keeper_phone` TEXT, `p_reg_operator` TEXT, `p_operator_phone` TEXT, `p_container_type` TEXT NOT NULL, `p_container_volume` REAL NOT NULL, `p_container_count` INTEGER NOT NULL, `p_time_from` TEXT NOT NULL, `p_time_to` TEXT NOT NULL, `p_status` INTEGER NOT NULL, PRIMARY KEY(`p_kp_id`));
        """.trimIndent()
        )
    }
}

class Migration1011 : Migration(10, 11) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `photo_types` (`pt_id` INTEGER NOT NULL, `pt_description` TEXT NOT NULL, `pt_short_name` TEXT NOT NULL, `pt_is_error` INTEGER NOT NULL, PRIMARY KEY(`pt_id`));
        """.trimIndent()
        )
    }
}

class Migration1112 : Migration(11, 12) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `contacts` (`c_id` INTEGER NOT NULL, `c_name` TEXT, `c_phone` TEXT, `c_when_logged` TEXT, PRIMARY KEY(`c_id`));
        """.trimIndent()
        )
    }
}