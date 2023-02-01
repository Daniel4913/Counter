package com.example.counter.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.counter.data.ActivityDao
import com.example.counter.data.DescriptionDao
import com.example.counter.data.OccurrenceDao
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Description
import com.example.counter.data.modelentity.Occurrence

@Database(
    entities = [
        Occurrence::class,
        Activity::class,
        Description::class],
    version = 3,
    exportSchema = false
)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun occurrenceDao(): OccurrenceDao
    abstract fun activityDao(): ActivityDao
    abstract fun descriptionDao(): DescriptionDao

}