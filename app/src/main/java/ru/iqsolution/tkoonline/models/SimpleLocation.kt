package ru.iqsolution.tkoonline.models

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.io.Serializable
import java.util.*
import kotlin.math.roundToInt

class SimpleLocation : Serializable, Location<Double> {

    override var latitude = 0.0

    override var longitude = 0.0

    var altitude = 0

    var satellites = 0

    /**
     * In meters
     */
    var accuracy = 0f

    var direction = 0

    /**
     * It's only the fixed mileage at location time
     */
    var distance = 0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    val locationTime: DateTime

    /**
     * признак валидности координат 1-валидные 0 - не валидные
     * Не валидные считаются координаты полученные более 5 секунд назад или с погрешностью более 30 метров
     */
    val validity: Int
        get() {
            val zone = DateTimeZone.forTimeZone(TimeZone.getDefault())
            if (accuracy <= 30 && System.currentTimeMillis() - locationTime.withZone(zone).millis <= 5000L) {
                return 1
            }
            return 0
        }

    constructor(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
        locationTime = DateTime.now()
    }

    constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())

    constructor(location: android.location.Location) {
        latitude = location.latitude
        longitude = location.longitude
        altitude = location.altitude.roundToInt()
        accuracy = location.accuracy
        locationTime = DateTime.now()
    }
}