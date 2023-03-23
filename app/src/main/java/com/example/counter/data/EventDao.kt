package com.example.counter.data

import androidx.room.*
import com.example.counter.data.modelentity.Event
import com.example.counter.data.relations.EventWithEventLogs
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * from events_table ORDER BY createDate ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * from events_table WHERE eventId = :id")
    fun getEvent(id: Int): Flow<Event>

    @Transaction
    @Query("SELECT * from events_table")
    fun getAllEventsWithEventLogs(): Flow<List<EventWithEventLogs>>

    @Transaction
    @Query("SELECT * from events_table WHERE category = :category")
    fun getEventsByCategory(category: String): Flow<List<EventWithEventLogs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun delete(event: Event)
    @Query("DELETE from events_table")
    suspend fun deleteAll()
}