package com.example.counter.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Occurence(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "date")
    val createDate: String,
    @ColumnInfo(name ="occur")
    val occurMore: Boolean,
    @ColumnInfo(name = "category")
    val category: String,
) {
}