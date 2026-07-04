package com.example.sendready.core.domain.model

data class CompressionProgressStats(
    val overallProgressPercent: Int = 0,
    val currentFileProgressPercent: Int = 0,
    val currentFileIndex: Int = 0,
    val totalFiles: Int = 0,
    val currentFileName: String = "",
    val currentFileOriginalSizeBytes: Long = 0L,
    val speedBytesPerSecond: Long = 0L,
    val estimatedRemainingTimeMs: Long = 0L,
    val elapsedTimeMs: Long = 0L,
    val bytesSavedSoFar: Long = 0L
)

sealed class CompressionState {
    data object Idle : CompressionState()
    
    data class Compressing(
        val stats: CompressionProgressStats
    ) : CompressionState()
    
    data class Paused(
        val stats: CompressionProgressStats
    ) : CompressionState()
    
    data class Finished(
        val results: List<CompressionResult>,
        val totalOriginalSizeBytes: Long,
        val totalCompressedSizeBytes: Long,
        val totalTimeMs: Long
    ) : CompressionState()
    
    data class Failed(
        val error: Throwable,
        val partialResults: List<CompressionResult>
    ) : CompressionState()
}
