package com.example.counter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OccurenceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(occurence: Occurence)

    @Update
    suspend fun update(occurence: Occurence)

    @Delete
    suspend fun delete(occurence: Occurence)

    @Query("SELECT * from occurence WHERE id = :id")
    fun getOccurence(id: Int): Flow<Occurence>

    @Query("SELECT * from occurence ORDER BY date ASC")
    fun getOccurencies(): Flow<List<Occurence>>
}