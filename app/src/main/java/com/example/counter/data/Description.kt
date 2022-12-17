package com.example.counter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Description(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "description_id")
    val descriptionId: Int = 0,

    @ColumnInfo(name = "occurence_owner_id")
    val occurenceOwnerId: Int = 0,

    @ColumnInfo (name = "description_date")
    val descriptionDate: String,

    @ColumnInfo (name = "description_note")
    val descriptionNote : String
)