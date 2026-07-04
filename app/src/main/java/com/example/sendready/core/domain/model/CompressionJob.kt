package com.example.sendready.core.domain.model

enum class JobStatus {
    PENDING,
    PREPARING,
    COMPRESSING,
    COMPLETED,
    FAILED,
    CANCELED
}

data class CompressionJob(
    val id: String,
    val metadata: VideoMetadata,
    val preset: CompressionPreset = CompressionPreset.Balanced,
    val estimate: CompressionEstimate?,
    val status: JobStatus = JobStatus.PENDING,
    val progress: Float = 0f
)
