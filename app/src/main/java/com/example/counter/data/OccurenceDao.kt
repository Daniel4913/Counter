package com.example.counter.data

import androidx.room.*
import com.example.counter.data.relations.OccurrenceWithDatesTimes
import com.example.counter.data.relations.OccurrenceWithDescripion
import kotlinx.coroutines.flow.Flow

@Dao
interface OccurenceDao {
    @Query("SELECT * from occurence ORDER BY create_date ASC")
    fun getOccurencies(): Flow<List<Occurence>>

    @Query("SELECT * from occurence WHERE occurence_id = :id")
    fun getOccurence(id: Int): Flow<Occurence>

    @Query("SELECT * from occurence WHERE occurence_id = :id")
    fun getOccurenceId(id: Int): Int

    @Transaction
    @Query("SELECT * from occurence")
    fun getOccurrencesWithDatesTimes(): Flow<List<OccurrenceWithDatesTimes>>

    @Transaction
    @Query("SELECT * from description WHERE occurence_owner_id = :id")
    fun getOccurrencesWithDescriptions(id: Int): Flow<List<Description>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOccurence(occurence: Occurence)

    @Update
    suspend fun update(occurence: Occurence)

//    @Update
//    suspend fun updateOccurrenceWithDateTime(occurrenceWithDatesTimes: OccurrenceWithDatesTimes)

    @Delete
    suspend fun delete(occurence: Occurence)
}