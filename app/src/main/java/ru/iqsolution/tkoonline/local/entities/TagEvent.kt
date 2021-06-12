package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.extensions.Pattern
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import timber.log.Timber
import java.util.*
import kotlin.experimental.and

@Entity(
    tableName = "tag_events",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["te_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["te_token_id"])
    ]
)
class TagEvent : Container {

    @PrimaryKey
    @ColumnInfo(name = "te_id")
    var id = 0L

    @ColumnInfo(name = "te_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "te_kp_id")
    override var kpId = 0

    @NonNull
    @Pattern(Pattern.DATETIME_ZONE)
    @ColumnInfo(name = "te_when_time")
    lateinit var whenTime: DateTime

    @NonNull
    @ColumnInfo(name = "te_container_type")
    override lateinit var containerType: String

    @ColumnInfo(name = "te_container_volume")
    override var containerVolume = 0f

    @Ignore
    override var containerCount = 0

    companion object {

        fun parseText(data: ByteArray?): TagEvent? {
            if (data == null) {
                return null
            }
            try {
                val encoding = if (data[0] and 128.toByte() == 0.toByte()) "UTF-8" else "UTF-16"
                val langLength = data[0] and 63.toByte()
                val text = String(data, 1 + langLength, data.size - 1 - langLength, charset(encoding))
                val (ownId, type, incVolume) = text.toUpperCase(Locale.getDefault())
                    .split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)".toRegex())
                return TagEvent().apply {
                    id = ownId.toLong()
                    containerType = when {
                        type.matches("^[ТT](Б[ОO]?)?\$".toRegex()) -> ContainerType.REGULAR.id
                        type.matches("^[КK](Г[МM])?\$".toRegex()) -> ContainerType.BUNKER.id
                        type.matches("^Б[ТT]?\$".toRegex()) -> ContainerType.BULK1.id
                        type.matches("^[СC](П[ЕE]Ц.?)?\$".toRegex()) -> ContainerType.SPECIAL1.id
                        else -> ContainerType.UNKNOWN.id
                    }
                    containerVolume = incVolume.toFloat() / 10
                    whenTime = DateTime.now()
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
            return null
        }
    }
}