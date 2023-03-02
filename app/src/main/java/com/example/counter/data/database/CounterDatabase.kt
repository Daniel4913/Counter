package com.example.counter.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.counter.data.ActivityDao
import com.example.counter.data.OccurrenceDao
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence

@Database(
    entities = [
        Occurrence::class,
        Activity::class,
        ],
    version = 10,
    exportSchema = false
)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun occurrenceDao(): OccurrenceDao
    abstract fun activityDao(): ActivityDao
    }