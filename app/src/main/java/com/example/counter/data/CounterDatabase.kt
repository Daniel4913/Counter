package com.example.counter.data


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Description
import com.example.counter.data.modelentity.Occurrence

@Database(
    entities = [
        Occurrence::class,
        Activity::class,
        Description::class],
    version = 2,
    exportSchema = false
)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun occurrenceDao(): OccurrenceDao
    abstract fun activityDao(): ActivityDao
    abstract fun descriptionDao(): DescriptionDao

}