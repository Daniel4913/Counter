package com.example.counter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.counter.data.modelentity.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Transaction
    @Query("SELECT * from activities_table WHERE occurrence_owner_id = :id ORDER BY date_time_id DESC")
    fun getOccurenceWithActivities(id:Int): Flow<List<Activity>>

    @Transaction
    @Query("SELECT * from activities_table")
    fun getAllActivities(): Flow<List<Activity>>

//    @Query("SELECT date_time_id, full_date from dateTime WHERE occurence_owner_id = :id")
//@Query("SELECT date_time_id, full_date from dateTime")
//    fun getLastDateTime(id: Int): Flow<List<DateTime>>

    @Query("UPDATE activities_table SET seconds_to_next =:secondsTo AND seconds_from_last =:secondsFrom WHERE date_time_id =:activityId")
    suspend fun updateSeconds(activityId: Int, secondsFrom: Long, secondsTo: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)

    @Update
    suspend fun update(activity: Activity)

    @Delete
    suspend fun deleteAll(activity: Activity)

    @Query("DELETE from activities_table")
    suspend fun deleteAll()
}