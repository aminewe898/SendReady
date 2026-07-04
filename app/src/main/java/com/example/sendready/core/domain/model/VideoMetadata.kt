package com.example.sendready.core.domain.model

data class VideoMetadata(
    val uriString: String,
    val filename: String,
    val sizeBytes: Long,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val bitrate: Int,
    val fps: Float,
    val codec: String,
    val container: String
) {
    val resolution: String
        get() = "${width}x${height}"
}
