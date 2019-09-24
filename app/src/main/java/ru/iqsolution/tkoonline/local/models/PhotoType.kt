package ru.iqsolution.tkoonline.local.models

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "photo_types",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["id"],
            childColumns = ["token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class PhotoType {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "token_id", index = true)
    var tokenId = 0L

    @ColumnInfo(name = "type_id")
    @SerializedName("id")
    var typeId = 0

    @ColumnInfo(name = "description")
    @SerializedName("description")
    lateinit var description: String

    @ColumnInfo(name = "is_error")
    @SerializedName("is_error")
    var isError = 0

    @Embedded
    var token: Token? = null

    @Suppress("unused")
    enum class Default(
        val id: Int,
        val description: String,
        val isDelete: Boolean,
        val isError: Boolean,
        val shortName: String
    ) {
        BEFORE(0, "До", false, false, "До"),
        AFTER(1, "После", false, false, "Посл"),
        ROAD(11, "Нет проезда", false, true, "НетПр"),
        CONTRACT(12, "Мусор не соответствует договору", false, true, "МнСД"),
        EMPTY(13, "Пустой бак", false, true, "Пуст"),
        DAMAGED(14, "Контейнер повреждён", false, true, "Повр"),
        DIRTY(15, "Площадка требует уборки", false, true, "ГрПл"),
        OTHER(16, "Прочее", false, true, "Пр");

        companion object {

            private val map = values().associateBy(Default::id)

            fun fromId(value: Int): Default? = map[value]
        }
    }
}