package com.example.sendready.core.domain.model

data class CompressionEstimate(
    val estimatedSizeBytes: Long,
    val estimatedTimeMs: Long,
    val originalSizeBytes: Long
) {
    val savingsBytes: Long
        get() = (originalSizeBytes - estimatedSizeBytes).coerceAtLeast(0)

    val compressionRatio: Float
        get() = if (originalSizeBytes > 0) {
            (savingsBytes.toFloat() / originalSizeBytes.toFloat()) * 100f
        } else 0f
}
