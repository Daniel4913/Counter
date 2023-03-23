package com.example.counter.data.modelentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.counter.util.Constants.Companion.EVENTS_LOGS_TABLE

@Entity(tableName = EVENTS_LOGS_TABLE)
data class EventLog(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="event_log_id")
    val eventLogId: Int = 0,

    @ColumnInfo(name="event_owner_id")
    val eventOwnerId: Int =0,

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