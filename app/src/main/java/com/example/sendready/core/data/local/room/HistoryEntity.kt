package com.example.sendready.core.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compression_history")
data class HistoryEntity(
    @PrimaryKey
    val id: String,
    val dateMs: Long,
    val filename: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val compressionRatio: Float,
    val presetId: String,
    val codec: String,
    val durationMs: Long,
    val outputPath: String,
    val originalUri: String,
    val thumbnailUri: String?
)
