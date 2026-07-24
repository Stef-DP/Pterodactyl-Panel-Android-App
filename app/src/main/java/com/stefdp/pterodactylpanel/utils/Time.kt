package com.stefdp.pterodactylpanel.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun formatMs(
    ms: Double,
    abbreviated: Boolean = true,
    limit: Int = 7
): String {
    val duration = ms.toLong()

    val secondsShortSuffix = "s"
    val minutesShortSuffix = "m"
    val hoursShortSuffix = "h"
    val daysShortSuffix = "d"
    val monthsShortSuffix = "mo"
    val yearsShortSuffix = "y"

    if (duration == 0L) return "0$secondsShortSuffix"

    val second = 1.seconds.inWholeMilliseconds // 1000L
    val minute = 1.minutes.inWholeMilliseconds // second * 60
    val hour = 1.hours.inWholeMilliseconds // minute * 60
    val day = 1.days.inWholeMilliseconds // hour * 24
    val month = day * 30
    val year = day * 365

    val years = duration / year
    val months = (duration % year) / month
    val days = (duration % month) / day
    val hours = (duration % day) / hour
    val minutes = (duration % hour) / minute
    val seconds = (duration % minute) / second

    val secondsLongSuffix = if (seconds > 1) "seconds" else "second"
    val minutesLongSuffix = if (minutes > 1) "minutes" else "minute"
    val hoursLongSuffix = if (hours > 1) "hours" else "hour"
    val daysLongSuffix = if (days > 1) "days" else "day"
    val monthsLongSuffix = if (months > 1) "months" else "month"
    val yearsLongSuffix = if (years > 1) "years" else "year"

    val dateSegments = listOfNotNull(
        if (years > 0) "${years}${if (abbreviated) yearsShortSuffix else yearsLongSuffix}" else null,
        if (months > 0) "${months}${if (abbreviated) monthsShortSuffix else monthsLongSuffix}" else null,
        if (days > 0) "${days}${if (abbreviated) daysShortSuffix else daysLongSuffix}" else null,
        if (hours > 0) "${hours}${if (abbreviated) hoursShortSuffix else hoursLongSuffix}" else null,
        if (minutes > 0) "${minutes}${if (abbreviated) minutesShortSuffix else minutesLongSuffix}" else null,
        if (seconds > 0) "${seconds}${if (abbreviated) secondsShortSuffix else secondsLongSuffix}" else null
    )

    return dateSegments.take(limit).joinToString(" ")
}

fun formatDate(
    date: String,
    short: Boolean = false,
    dateOnly: Boolean = false,
    timeOnly: Boolean = false,
): String {
    if (short) {
        val pattern = when {
            dateOnly -> "yyyy-MM-dd"
            timeOnly -> "hh:mm a"
            else -> "yyyy-MM-dd, hh:mm a"
        }

        return Instant
            .parse(date)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(pattern))
    }

    val dateTime = Instant
        .parse(date)
        .atZone(ZoneId.systemDefault())

    val day = dateTime.dayOfMonth

    val suffix = getOrdinalSuffix(day)

    val pattern = when {
        dateOnly -> "MMM d'$suffix'"
        timeOnly -> "hh:mm a"
        else -> "MMM d'$suffix', hh:mm a"
    }

    return dateTime.format(
        DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    )
}

private fun getOrdinalSuffix(day: Int): String {
    if (day in 11..13) return "th"

    return when (day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}