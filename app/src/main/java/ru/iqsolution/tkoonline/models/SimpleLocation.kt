package ru.iqsolution.tkoonline.models

import org.joda.time.DateTime
import java.io.Serializable
import kotlin.math.roundToInt

@Suppress("LeakingThis")
open class SimpleLocation : Serializable, Location<Double> {

    override var latitude = 0.0

    override var longitude = 0.0

    var altitude = 0

    var satellites = 0

    /**
     * It's only needed for map
     * In meters
     */
    var accuracy = 0f

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    val locationTime: DateTime

    /**
     * признак валидности координат 1-валидные 0 - не валидные
     * Не валидные считаются координаты полученные более 5 секунд назад или с погрешностью более 30 метров
     */
    val validity: Int
        get() = if (accuracy <= 30) 1 else 0

    constructor(lat: Double, lon: Double, datetime: DateTime) {
        latitude = lat
        longitude = lon
        locationTime = datetime
    }

    constructor(lat: Double, lon: Double) : this(lat, lon, DateTime.now())

    constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())

    constructor(location: android.location.Location) {
        latitude = location.latitude
        longitude = location.longitude
        altitude = location.altitude.roundToInt()
        accuracy = location.accuracy
        locationTime = DateTime.now()
    }

    override fun toString(): String {
        return "SimpleLocation(" +
            "latitude=$latitude, " +
            "longitude=$longitude, " +
            "altitude=$altitude, " +
            "satellites=$satellites, " +
            "accuracy=$accuracy, " +
            "locationTime=$locationTime" +
            ")"
    }
}