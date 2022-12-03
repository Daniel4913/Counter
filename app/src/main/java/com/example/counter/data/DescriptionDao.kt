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
interface DescriptionDao {
    @Transaction
    @Query("SELECT * FROM description WHERE occurence_owner_id = :id ORDER BY description_id DESC")
    fun getOccurenceWithDescriptions(id:Int): Flow<List<Description>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDescription(description: Description)

    @Update
    suspend fun update(description: Description)

    @Delete
    suspend fun delete(description: Description)
}