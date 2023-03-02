package com.example.counter.data.modelentity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.counter.data.modelentity.Activity
import com.example.counter.util.Constants.Companion.OCCURRENCES_TABLE

@Entity(tableName = OCCURRENCES_TABLE)
data class Occurrence(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="occurrence_id")
    val occurrenceId: Int = 0,

    @ColumnInfo(name = "occurrence_name")
    val occurrenceName: String,

    @ColumnInfo(name = "create_date")
    val createDate: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "interval_frequency")
    val intervalFrequency: String

    )