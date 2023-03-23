package com.example.counter.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.counter.data.EventLogDao
import com.example.counter.data.EventDao
import com.example.counter.data.modelentity.EventLog
import com.example.counter.data.modelentity.Event

@Database(
    entities = [
        Event::class,
        EventLog::class,
        ],
    version = 1,
    exportSchema = false
)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun eventLogDao(): EventLogDao
    }