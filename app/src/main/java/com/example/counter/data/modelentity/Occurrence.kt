package com.example.counter.data.modelentity


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.counter.util.Constants.Companion.OCCURRENCES_TABLE
import kotlinx.parcelize.Parcelize

@Entity(tableName = OCCURRENCES_TABLE)
@Parcelize
data class Occurrence(
    @PrimaryKey(autoGenerate = true)
    val occurrenceId: Int = 0,
    val occurrenceIcon: String,
    val occurrenceName: String,
    val createDate: String,
    val category: Category,
    val intervalFrequency: String,
    var status: CounterStatus? = null
) : Parcelable
