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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDescription(description: Description)

    @Update
    suspend fun updateDescription(description: Description)

    @Delete
    suspend fun deleteDescription(description: Description)
}