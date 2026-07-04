package com.example.sendready.core.domain.model

data class CompressionResult(
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
