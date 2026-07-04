package com.example.sendready.core.data.repository

import com.example.sendready.core.data.local.room.HistoryDao
import com.example.sendready.core.data.local.room.HistoryEntity
import com.example.sendready.core.domain.model.CompressionResult
import com.example.sendready.core.domain.repository.CompressionHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CompressionHistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : CompressionHistoryRepository {

    override fun getAllHistory(): Flow<List<CompressionResult>> {
        return historyDao.getAllHistory().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun saveResult(result: CompressionResult) {
        historyDao.insertHistory(result.toEntity())
    }

    override suspend fun deleteResult(id: String) {
        historyDao.deleteHistory(id)
    }

    override suspend fun clearHistory() {
        historyDao.clearAll()
    }

    private fun HistoryEntity.toDomainModel() = CompressionResult(
        id = id,
        dateMs = dateMs,
        filename = filename,
        originalSizeBytes = originalSizeBytes,
        compressedSizeBytes = compressedSizeBytes,
        compressionRatio = compressionRatio,
        presetId = presetId,
        codec = codec,
        durationMs = durationMs,
        outputPath = outputPath,
        thumbnailUri = thumbnailUri
    )

    private fun CompressionResult.toEntity() = HistoryEntity(
        id = id,
        dateMs = dateMs,
        filename = filename,
        originalSizeBytes = originalSizeBytes,
        compressedSizeBytes = compressedSizeBytes,
        compressionRatio = compressionRatio,
        presetId = presetId,
        codec = codec,
        durationMs = durationMs,
        outputPath = outputPath,
        thumbnailUri = thumbnailUri
    )
}
