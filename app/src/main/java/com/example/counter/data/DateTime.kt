package com.example.counter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DateTime(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="date_time_id")
    val dateTimeId: Int = 0,

    @ColumnInfo(name="occurence_owner_id")
    val occurenceOwnerId: Int,

    @ColumnInfo(name="full_date")
    val fullDate: String,

    @ColumnInfo(name= "time_start")
    val timeStart: String?,

    @ColumnInfo(name="time_stop")
    val timeStop: String?,

    @ColumnInfo(name="total_time")
    val totalTime: String?,
){
}