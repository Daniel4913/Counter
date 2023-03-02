package com.example.counter.data.modelentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.counter.util.Constants.Companion.ACTIVITIES_TABLE

@Entity(tableName = ACTIVITIES_TABLE)
data class Activity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="activity_id")
    val activityId: Int = 0,

    @ColumnInfo(name="occurrence_owner_id")
    val occurrenceOwnerId: Int =0,

    @ColumnInfo(name="full_date")
    val fullDate: String,

    @ColumnInfo(name="seconds_passed")
    val secondsPassed: Long?,

    @ColumnInfo(name="interval_seconds")
    var intervalSeconds: Long?,

    @ColumnInfo(name="seconds_to_next")
    var secondsToNext: Long? = -1
){
}