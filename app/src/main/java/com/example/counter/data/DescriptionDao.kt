package com.example.counter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.counter.data.modelentity.Description
import kotlinx.coroutines.flow.Flow

@Dao
interface DescriptionDao {
    @Transaction
    @Query("SELECT * from descriptions_table WHERE occurrence_owner_id = :id")
    fun getDescriptions(id: Int): Flow<List<Description>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDescription(description: Description)

    @Update
    suspend fun updateDescription(description: Description)

    @Delete
    suspend fun deleteDescription(description: Description)

    @Query("DELETE from descriptions_table")
        suspend fun deleteAll()

}