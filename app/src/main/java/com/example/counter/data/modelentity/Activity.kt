package com.example.counter.data.modelentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.counter.util.Constants.Companion.ACTIVITIES_TABLE

@Entity(tableName = ACTIVITIES_TABLE)
data class Activity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="date_time_id")
    val dateTimeId: Int = 0,

    @ColumnInfo(name="occurrence_owner_id")
    val occurrenceOwnerId: Int =0,

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