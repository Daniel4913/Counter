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
    val occurenceOwnerId: Int =0,

    @ColumnInfo(name="full_date")
    val fullDate: String,

    @ColumnInfo(name= "time_spend")
    val timeSpend: String?,

    @ColumnInfo(name="seconds_from_last")
    val secondsFromLast: Long?,

    @ColumnInfo(name="seconds_to_next")
    val secondsToNext: Long?,
){
}