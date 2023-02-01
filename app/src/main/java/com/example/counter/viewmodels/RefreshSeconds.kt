package com.example.counter.viewmodels
//
//import com.example.counter.data.relations.OccurrenceWithActivities
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//class RefreshSeconds(private val occ: OccurrenceWithActivities) {
//
//    val lastActivity = occ.occurrenceActivities[0]
//    val timeCounter:  TimeCounter
//
//    companion object{
//
//    fun refresh(): Long{
//
//        val secondsPassed = lastActivity.secondsFromLast
//
//        val refreshedSecondsTo = 3600 - secondsPassed!!
//        return refreshedSecondsTo
//    }
//    }
//
//
//
//
//}