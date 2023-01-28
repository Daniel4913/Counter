package com.example.counter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Occurence::class,
        DateTime::class,
        Description::class],
    version = 4,
    exportSchema = false
)
abstract class CounterRoomDatabase : RoomDatabase() {
    abstract fun occurenceDao(): OccurenceDao
    abstract fun dateTimeDao(): DateTimeDao
    abstract fun descriptionDao(): DescriptionDao

    companion object {
        @Volatile
        private var INSTANCE: CounterRoomDatabase? = null

        fun getDatabase(context: Context): CounterRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CounterRoomDatabase::class.java,
                    "counter_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }
}