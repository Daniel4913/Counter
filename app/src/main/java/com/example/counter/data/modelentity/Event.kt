package com.example.counter.data.modelentity


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.counter.util.Constants.Companion.EVENTS_TABLE
import kotlinx.parcelize.Parcelize

@Entity(tableName = EVENTS_TABLE)
@Parcelize
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,
    val eventIcon: String,
    val eventName: String,
    val createDate: String,
    val category: Category,
    val intervalFrequency: String,
    var status: CounterStatus? = null
) : Parcelable
