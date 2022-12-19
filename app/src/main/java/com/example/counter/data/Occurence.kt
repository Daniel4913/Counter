package com.example.counter.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Occurence(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="occurence_id")
    val occurenceId: Int = 0,

    @ColumnInfo(name = "occurence_name")
    val occurenceName: String,

    @ColumnInfo(name = "create_date")
    val createDate: String,

    @ColumnInfo(name ="occur_more")
    val occurMore: Boolean,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "description_id")
    val descriptionId: Int = 0,

    @ColumnInfo(name = "interval_value")
    val intervalValue: Int,

    @ColumnInfo(name = "interval_frequency")
    val intervalFrequency: String = "hours"

    )
