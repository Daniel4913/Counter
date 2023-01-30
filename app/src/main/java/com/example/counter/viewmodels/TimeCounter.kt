package com.example.counter.viewmodels

import com.example.counter.util.Constants
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds

class TimeCounter(occurrence: Occurrence, activity: Activity) {

    private val occurrence = occurrence
    private val lastDateTime = activity.fullDate

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


    fun calculateSecondsTo(secondsTo: Long): Long {
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

    }


    fun secondsToComponents(secondsPassed: Long): String {
        secondsPassed.seconds.toComponents { days, hours, minutes, seconds, nanoseconds ->
            var calculated = ""

             when (days) {
                0L -> calculated = "${hours}h ${minutes}m"
                else -> calculated =  "${days}d ${hours}h ${minutes}m"
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
         **/
        val secondsPassed = getSecondsPassed()
        val secondsTo = getSecondsTo()




    }

}