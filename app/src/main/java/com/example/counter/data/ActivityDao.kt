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
    @Query("SELECT * from activities_table WHERE occurrence_owner_id = :id ORDER BY activity_id DESC")
    fun getOccurrenceWithActivities(id:Int): Flow<List<Activity>>

    @Transaction
    @Query("SELECT * from activities_table")
    fun getAllActivities(): Flow<List<Activity>>

    @Query("UPDATE activities_table SET seconds_to_next =:secondsTo AND seconds_passed =:secondsPassed WHERE activity_id =:activityId")
    suspend fun updateSeconds(activityId: Int, secondsPassed: Long, secondsTo: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)

    @Transaction
    @Update
    suspend fun update(activity: Activity)

    @Delete
    suspend fun deleteAll(activity: Activity)

    @Query("DELETE from activities_table")
    suspend fun deleteAll()
    @Transaction
    @Query("SELECT * from activities_table WHERE activity_id=:id")
    fun getActivity(id: Int): Flow<Activity>
}