package com.example.counter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Occurence::class], version = 1, exportSchema = false)
abstract class OccurencyRoomDatabase: RoomDatabase() {
    abstract fun occurenceDao(): OccurenceDao

    companion object {
        @Volatile
        private var INSTANCE: OccurencyRoomDatabase? = null

        fun getDatabase(context: Context): OccurencyRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OccurencyRoomDatabase::class.java,
                    "occurence_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
//                return instance
                instance
            }

        }
    }
}