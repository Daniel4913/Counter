package com.example.counter.util

import com.example.counter.data.modelentity.EventLog
import com.example.counter.data.modelentity.Event
import com.example.counter.util.Constants.Companion.DEFAULT_FORMATTER
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds

class TimeCounter(event: Event, eventLog: EventLog? = null) {


    private val lastDateTime = eventLog!!.fullDate
    val getIntervalSeconds = eventLog!!.intervalSeconds

    companion object {
        fun calculateIntervalToSeconds(interval: String): Long {
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
    }

    fun getSecondsTo(secondsTo: Long): Long {
        val timeFrom = lastDateTime
        val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMATTER)
        val lastDate = LocalDateTime.parse(timeFrom, formatter)
        val calculatedToDay = lastDate.plusSeconds(secondsTo)

        return ChronoUnit.SECONDS.between(
            LocalDateTime.now(),
            calculatedToDay,
        )
    }

    fun getSecondsPassed(): Long {
        val today = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMATTER)
        val lastDate = LocalDateTime.parse(lastDateTime, formatter)

        return ChronoUnit.SECONDS.between(
            lastDate,
            today
        )
    }


    fun secondsToComponents(seconds: Long): String {
        seconds.seconds.toComponents { days, hours, minutes, _, _ ->
            val calculated = when (days) {
                0L -> "${hours}h ${minutes}m"
                else -> "${days}d ${hours}h ${minutes}m"
            }

            return calculated
        }
    }


}