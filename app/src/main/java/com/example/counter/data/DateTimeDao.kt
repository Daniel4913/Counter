package com.example.counter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DateTimeDao {
    @Transaction
    @Query("SELECT * from dateTime WHERE occurence_owner_id = :id ORDER BY odate_time_id DESC")
    fun getOccurenceWithDatesTimes(id:Int): Flow<List<DateTime>>
    //ORDER BY

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateTime(dateTime: DateTime)

    @Update
    suspend fun update(dateTime: DateTime)

    @Delete
    suspend fun delete(dateTime: DateTime)
}