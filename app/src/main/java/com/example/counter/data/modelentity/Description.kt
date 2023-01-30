package com.example.counter.data.modelentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.counter.util.Constants.Companion.DESCRIPTIONS_TABLE

@Entity(tableName = DESCRIPTIONS_TABLE)
data class Description(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "description_id")
    val descriptionId: Int = 0,

    @ColumnInfo(name = "occurrence_owner_id")
    val occurrenceOwnerId: Int = 0,

    @ColumnInfo (name = "description_date")
    val descriptionDate: String,

    @ColumnInfo (name = "description_note")
    val descriptionNote : String
)