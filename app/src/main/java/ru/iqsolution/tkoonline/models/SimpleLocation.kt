package ru.iqsolution.tkoonline.models

import org.joda.time.DateTime
import java.io.Serializable
import kotlin.math.roundToInt

class SimpleLocation : Serializable, Location<Double> {

    override var latitude = 0.0

    override var longitude = 0.0

    var altitude = 0

    /**
     * признак валидности координат 1-валидные 0 - не валидные
     * Не валидные считаются координаты полученные более 5 секунд назад или с погрешностью более 30 метров
     */
    var validity = 0

    /**
     * In meters
     */
    var accuracy = 0f

    var satellites = 0

    /**
     * km/h
     */
    var speed = 0

    var direction = 0

    /**
     * In meters
     */
    var mileage = 0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    var locationTime: DateTime

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