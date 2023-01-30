package com.example.counter.data

import androidx.room.*
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.data.relations.OccurrenceWithDescripion
import kotlinx.coroutines.flow.Flow

@Dao
interface OccurrenceDao {
    @Query("SELECT * from occurrences_table ORDER BY create_date ASC")
    fun getOccurrences(): Flow<List<Occurrence>>

    @Query("SELECT * from occurrences_table WHERE occurrence_id = :id")
    fun getOccurrence(id: Int): Flow<Occurrence>

    @Transaction
    @Query("SELECT * from occurrences_table")
    fun getOccurrencesWithActivities(): Flow<List<OccurrenceWithActivities>>

    @Transaction
    @Query("SELECT * from occurrences_table WHERE category = :category")
    fun getOccurrencesByCategory(category: String): Flow<List<OccurrenceWithActivities>>

    @Transaction
    @Query("SELECT * from occurrences_table")
    fun getOccurrencesWithDescriptions(): Flow<List<OccurrenceWithDescripion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOccurrence(occurrence: Occurrence)

    @Update
    suspend fun updateOccurrence(occurrence: Occurrence)

    @Delete
    suspend fun delete(occurrence: Occurrence)
    @Query("DELETE from occurrences_table")
    suspend fun deleteAll()
}