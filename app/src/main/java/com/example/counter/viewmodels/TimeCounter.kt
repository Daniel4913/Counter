package com.example.counter.viewmodels

import com.example.counter.util.Constants
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds

class TimeCounter(private val occurrence: Occurrence, activity: Activity) {

    private val lastDateTime = activity.fullDate

    private val getIntervalSeconds = activity.intervalSeconds

    private val occurrenceActivities = OccurrenceWithActivities

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


    fun intervalSeconds(secondsTo: Long): Long {
        val timeFrom = lastDateTime
        val timeTo = secondsTo

        val pattern = "HH:mm:ss dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val lastDate = LocalDateTime.parse(timeFrom, formatter)
        val calculatedToDay = lastDate.plusSeconds(timeTo)
        val secondsTo = ChronoUnit.SECONDS.between(
            LocalDateTime.now(),
            calculatedToDay,
        )
        return secondsTo

    }

    fun refreshSecondsTo(intervalSeconds: Long, secondsPassed: Long): Long{
        return intervalSeconds-secondsPassed
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

    fun nieWykonane(){
            occurrenceActivities
    }


    fun secondsToComponents(secondsPassed: Long): String {
        secondsPassed.seconds.toComponents { days, hours, minutes, seconds, nanoseconds ->
            var calculated = ""

             when (days) {
                0L -> calculated = "${hours}h ${minutes}m ${seconds}s"
                else -> calculated =  "${days}d ${hours}h ${minutes}m ${seconds}s"
            }

            //TODO
//             when (hours) {
//                0 -> calculated = "${minutes}m ${seconds}m "
//                else -> calculated =  "${hours}h ${minutes}m"
//            }

            return calculated
        }
    }

    fun saveTimesToDb(){
        /**
        todo musze zapisac te sekundy, aby moc posortowac recycler view
         UPDATE: jednak to jest bez sensu bo bedzie zbyt duzo operacji na DB (zapisow i odczytow).
         Musze wyciagnac te czasy z textView
         UPDATE2: jednak chuj wie jak je wyciagnaz z tv wiec bede zapisywal w db.
        To dobre podejscie bo tylko jedno activity bede updateowal
         **/
        val secondsPassed = getSecondsPassed()
        val secondsTo = getSecondsTo()




    }

}