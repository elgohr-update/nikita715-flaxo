package org.flaxo.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.Transient
import kotlin.math.floor
import kotlin.math.roundToInt

private const val MILLIS_IN_DAY = 1000 * 60 * 60 * 24

/**
 * JavaScript implementation of a multiplatform datetime.
 */
@Serializable
actual class DateTime private constructor(@Transient private val date: kotlin.js.Date = kotlin.js.Date()) {

    @Serializer(forClass = DateTime::class)
    actual companion object : DateTimeSerializer {

        actual fun fromDateTimeString(string: String): DateTime = DateTime(kotlin.js.Date(string))

        actual fun now(): DateTime = DateTime(kotlin.js.Date())
    }

    actual fun toDateTimeString(): String = date.toISOString()

    actual fun toDateString(): String = toDateTimeString().substring(0, 10)

    actual operator fun compareTo(other: DateTime): Int = date.getTime().compareTo(other.date.getTime())

    /**
     * Returns a number of days between this and the [other] datetime.
     */
    fun daysUntil(other: DateTime): Int =
            floor(((other.date.getTime() - this.date.getTime()) / MILLIS_IN_DAY)).roundToInt()

    /**
     * Converts current date time to a human readable format string.
     */
    fun toHumanReadableString(): String = date.toDateString()
}
