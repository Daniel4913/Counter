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
interface ActivityDao {
    @Transaction
    @Query("SELECT * from activities_table WHERE occurrence_owner_id = :id ORDER BY full_date DESC")
    fun getOccurenceWithActivities(id:Int): Flow<List<Activity>>

//    @Query("SELECT date_time_id, full_date from dateTime WHERE occurence_owner_id = :id")
//@Query("SELECT date_time_id, full_date from dateTime")
//    fun getLastDateTime(id: Int): Flow<List<DateTime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)

    @Update
    suspend fun update(activity: Activity)

    @Delete
    suspend fun deleteAll(activity: Activity)

    @Query("DELETE from activities_table")
    suspend fun deleteAll()
}