package com.example.counter.util

import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.util.Constants.Companion.DEFAULT_FORMATTER
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds

class TimeCounter(private val occurrence: Occurrence, activity: Activity) {

    private val lastDateTime = activity.fullDate

    private val getIntervalSeconds = activity.intervalSeconds

//    private val occurrenceActivities = OccurrenceWithActivities

    // CALCULATING BLOK

    fun getIntervalSecondsTo(): Long {
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


    fun getSecondsTo(secondsTo: Long): Long {
        val timeFrom = lastDateTime
        val timeTo = secondsTo
        val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMATTER)
        val lastDate = LocalDateTime.parse(timeFrom, formatter)
        val calculatedToDay = lastDate.plusSeconds(timeTo)
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
        seconds.seconds.toComponents { days, hours, minutes, seconds, nanoseconds ->
            var calculated = ""

            when (days) {
                0L -> calculated = "${hours}h ${minutes}m ${seconds}s"
                else -> calculated = "${days}d ${hours}h ${minutes}m ${seconds}s"
            }

            //TODO
//             when (hours) {
//                0 -> calculated = "${minutes}m ${seconds}m "
//                else -> calculated =  "${hours}h ${minutes}m"
//            }

            return calculated
        }
    }


}