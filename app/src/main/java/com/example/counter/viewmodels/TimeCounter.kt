package com.example.counter.viewmodels

import com.example.counter.Constants
import com.example.counter.data.DateTime
import com.example.counter.data.Occurence
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds

open class TimeCounter(occurence: Occurence, dateTime: DateTime) {

    private val occurrence = occurence
    private val lastDateTime = dateTime.fullDate

    // CALCULATING BLOK

    fun getSecondsTo(): Long {
        val interval = occurrence.intervalFrequency

        val intervalValue = interval.split(" ")[0].toLong()
        val intervalFrequency = interval.split(" ")[1]
        var toSecondsTo: Long = 0
        when (intervalFrequency) {
            Constants.MINUTES -> {
                toSecondsTo = 60 * intervalValue
            }
            Constants.HOURS -> {
                toSecondsTo = 3600 * intervalValue
            }
            Constants.DAYS -> {
                toSecondsTo = 86400 * intervalValue

            }
            Constants.WEEKS -> {
                toSecondsTo = 604800 * intervalValue
            }
            Constants.MONTHS -> {
                toSecondsTo = 2629800 * intervalValue
            }
        }
        return toSecondsTo
    }


    private fun calculateSecondsTo(secondsTo: Long): Long {
        val timeFrom = lastDateTime
        val timeTo = secondsTo
        val pattern = "HH:mm:ss dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val lastDate = LocalDateTime.parse(timeFrom, formatter)
        val calculatedToDay = lastDate.plusSeconds(timeTo)
        val secondsTo = ChronoUnit.SECONDS.between(
            calculatedToDay,
            LocalDateTime.now(),
        )
        return secondsTo
    }


    fun getSecondsPassed(): Long {
        val today = LocalDateTime.now()
        val pattern = "HH:mm:ss dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val lastDate = LocalDateTime.parse(lastDateTime, formatter)
        val secondsPassed = ChronoUnit.SECONDS.between(
            lastDate,
            today
        )
        return secondsPassed
    }


    private fun secondsToComponents(secondsPassed: Long): String {
        secondsPassed.seconds.toComponents { days, hours, minutes, seconds, nanoseconds ->
            val calculated = when (days) {
                0L -> "${hours}h ${minutes}m ${seconds}s"
                else -> "${days}d ${hours}h ${minutes}m ${seconds}s"
            }
            return calculated
        }
    }

}