package com.example.counter.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OccurenceDao {
    @Query("SELECT * from occurence ORDER BY create_date ASC")
    fun getOccurencies(): Flow<List<Occurence>>

    @Query("SELECT * from occurence WHERE occurence_id = :id")
    fun getOccurence(id: Int): Flow<Occurence>

    @Query("SELECT * from occurence WHERE occurence_id = :id")
    fun getOccurenceId(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOccurence(occurence: Occurence)

    @Update
    suspend fun update(occurence: Occurence)

    @Delete
    suspend fun delete(occurence: Occurence)
}