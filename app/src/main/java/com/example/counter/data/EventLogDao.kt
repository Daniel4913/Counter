package com.example.counter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.counter.data.modelentity.EventLog
import kotlinx.coroutines.flow.Flow

@Dao
interface EventLogDao {
    @Transaction
    @Query("SELECT * from events_logs_table WHERE event_owner_id = :id ORDER BY event_log_id DESC")
    fun getEventWithEventLogs(id:Int): Flow<List<EventLog>>

    @Transaction
    @Query("SELECT * from events_logs_table")
    fun getAllEventLogs(): Flow<List<EventLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventLog(eventLog: EventLog)

    @Transaction
    @Update
    suspend fun update(eventLog: EventLog)

    @Delete
    suspend fun deleteEventLog(eventLog: EventLog)

    @Query("DELETE from events_logs_table WHERE event_owner_id=:eventOwnerId")
    suspend fun deleteEventLogs(eventOwnerId: Int)

    @Query("DELETE from events_logs_table")
    suspend fun deleteAllEventLogs()

    @Transaction
    @Query("SELECT * from events_logs_table WHERE event_log_id=:id")
    fun getEventLog(id: Int): Flow<EventLog>
}