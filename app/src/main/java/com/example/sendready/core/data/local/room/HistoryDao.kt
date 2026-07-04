package com.example.sendready.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM compression_history ORDER BY dateMs DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM compression_history WHERE id = :id")
    suspend fun deleteHistory(id: String)
    
    @Query("DELETE FROM compression_history")
    suspend fun clearAll()
}
