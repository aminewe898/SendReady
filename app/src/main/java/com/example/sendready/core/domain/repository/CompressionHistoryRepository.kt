package com.example.sendready.core.domain.repository

import com.example.sendready.core.domain.model.CompressionResult
import kotlinx.coroutines.flow.Flow

interface CompressionHistoryRepository {
    fun getAllHistory(): Flow<List<CompressionResult>>
    suspend fun saveResult(result: CompressionResult)
    suspend fun deleteResult(id: String)
    suspend fun clearHistory()
}
