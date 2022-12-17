package com.example.counter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Entity
data class DateTime(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="date_time_id")
    val dateTimeId: Int = 0,

    @ColumnInfo(name="occurence_owner_id")
    val occurenceOwnerId: Int =0,

    @ColumnInfo(name="full_date")
    val fullDate: String,

    @ColumnInfo(name= "time_start")
    val timeStart: String?,

    @ColumnInfo(name="time_stop")
    val timeStop: String?,

    @ColumnInfo(name="total_time")
    val totalTime: String?,
){
//    fun getLastDateTime(): Long {
//        val today = LocalDateTime.now()
//        val pattern = "HH:mm:ss dd.MM.yyyy"
//        val formatter = DateTimeFormatter.ofPattern(pattern)
//        val lastDate = LocalDateTime.parse(lastDateTime, formatter)
//        val hoursPassed = ChronoUnit.SECONDS.between(
//            lastDate,
//            today
//        )
//        return hoursPassed
//        //    fun corutine(){
//        //        GlobalScope.
//        //    }
//    }
}