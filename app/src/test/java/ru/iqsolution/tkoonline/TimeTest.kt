package ru.iqsolution.tkoonline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

private val day = TimeUnit.DAYS.toMillis(1)

private val minute = TimeUnit.MINUTES.toMillis(1)

private val today = LocalDate.of(2021, 1, 1)

private val time = LocalTime.of(2, 0)

private val moscowOffset = ZoneOffset.ofHours(3)

// 2021-01-01 02:00
private val mockClock = Clock.fixed(ZonedDateTime.of(today, time, moscowOffset).toInstant(), moscowOffset)

fun ZonedDateTime.isEarlier(offset: Long = 0L, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
    val now = ZonedDateTime.now(mockClock)
    val millis = unit.toMillis(offset)
    return if (millis > 0L) {
        now.minus(millis, ChronoUnit.MILLIS).isAfter(this)
    } else {
        now.isAfter(this)
    }
}

fun ZonedDateTime.isLater(offset: Long = 0L, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
    val now = ZonedDateTime.now(mockClock)
    val millis = unit.toMillis(offset)
    return if (millis > 0L) {
        now.plus(millis, ChronoUnit.MILLIS).isBefore(this)
    } else {
        now.isBefore(this)
    }
}

class TimeTest {

    @Test
    fun equals_test() {
        val now = ZonedDateTime.now(mockClock)
        assertEquals(now.toLocalDate(), today)
        assertNotEquals(now, now.withZoneSameInstant(ZoneOffset.UTC))
    }

    @Test
    fun duration_test() {
        // Также авторизация сбрасывается сервером ежедневно в 02:00 (UTC+3)
        val midnightOffset = ZoneOffset.ofHours(1)
        val now = ZonedDateTime.now(mockClock)
        val past = now.minusMinutes(1).withZoneSameInstant(midnightOffset)
        val future = now.plusMinutes(1).withZoneSameInstant(midnightOffset)
        assertEquals(ZoneOffset.UTC, ZoneOffset.ofHours(0))
        assert(Duration.between(past, past.plusDays(1).truncatedTo(ChronoUnit.DAYS)).toMillis() == minute)
        assert(Duration.between(now, now).toMillis() == 0L)
        assert(Duration.between(future, future.plusDays(1).with(LocalTime.ofSecondOfDay(0))).toMillis() == day - minute)
    }

    @Test
    fun isEarlier_test() {
        val now = ZonedDateTime.now(mockClock)
        assert(now.minusSeconds(1).isEarlier())
        assert(!now.withZoneSameInstant(ZoneOffset.UTC).isEarlier())
        assert(now.minusHours(1).isEarlier(minute))
        assert(!now.minusMinutes(1).withZoneSameInstant(ZoneOffset.UTC).isEarlier(minute))
        assert(!now.minusSeconds(1).isEarlier(minute))
        assert(!now.withZoneSameInstant(ZoneOffset.UTC).isEarlier(minute))
        assert(!now.plusSeconds(1).isEarlier())
    }

    @Test
    fun isLater_test() {
        val now = ZonedDateTime.now(mockClock)
        assert(now.plusSeconds(1).isLater())
        assert(!now.withZoneSameInstant(ZoneOffset.UTC).isLater())
        assert(now.plusHours(1).isLater(minute))
        assert(!now.plusMinutes(1).withZoneSameInstant(ZoneOffset.UTC).isLater(minute))
        assert(!now.plusSeconds(1).isLater(minute))
        assert(!now.withZoneSameInstant(ZoneOffset.UTC).isLater(minute))
        assert(!now.minusSeconds(1).isLater())
    }
}