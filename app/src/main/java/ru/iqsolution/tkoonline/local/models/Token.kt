package ru.iqsolution.tkoonline.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "tokens")
class Token {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "t_id")
    var id: Long? = null

    @ColumnInfo(name = "t_value")
    lateinit var value: String

    @ColumnInfo(name = "t_que_name")
    lateinit var queName: String

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "t_expires")
    lateinit var expires: DateTime
}