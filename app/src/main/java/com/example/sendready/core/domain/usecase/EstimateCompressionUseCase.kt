package com.example.sendready.core.domain.usecase

import com.example.sendready.core.domain.model.CompressionEstimate
import com.example.sendready.core.domain.model.CompressionPreset
import com.example.sendready.core.domain.model.VideoMetadata
import javax.inject.Inject
import kotlin.math.max

class EstimateCompressionUseCase @Inject constructor() {
    
    operator fun invoke(metadata: VideoMetadata, preset: CompressionPreset): CompressionEstimate {
        val estimatedBitrateKbps = getEstimatedBitrate(preset, metadata)
        
        // Size = (Bitrate(kbps) * 1000 / 8) * duration(s)
        val durationSeconds = metadata.durationMs / 1000f
        
        // Audio estimation (roughly 128kbps if we don't have exact)
        val audioBitrateKbps = 128
        val totalBitrateKbps = estimatedBitrateKbps + audioBitrateKbps
        
        var estimatedBytes = (totalBitrateKbps * 1000L / 8L) * durationSeconds.toLong()
        
        // If the original size is somehow smaller than the estimate, we assume no savings (unless forcefully scaling down)
        // For Smart Target Sizes, we force the bytes
        if (preset is CompressionPreset.SmartTargetSize) {
            estimatedBytes = preset.targetMb * 1024L * 1024L
        }

        // Apply some heuristics if hardcoded preset
        if (preset is CompressionPreset.DiscordFree) {
            estimatedBytes = 8L * 1024 * 1024
        }
        if (preset is CompressionPreset.DiscordNitro) {
            estimatedBytes = 50L * 1024 * 1024
        }
        if (preset is CompressionPreset.WhatsApp) {
            estimatedBytes = 16L * 1024 * 1024
        }
        if (preset is CompressionPreset.Email) {
            estimatedBytes = 25L * 1024 * 1024
        }
        if (preset is CompressionPreset.Signal) {
            estimatedBytes = 100L * 1024 * 1024
        }

        // Make sure we don't return an estimate larger than the original unless upscaling (rare)
        estimatedBytes = minOf(estimatedBytes, metadata.sizeBytes)

        // Very rough time estimate: assume 1x realtime for H264 1080p, faster for smaller, slower for H265/AV1
        val estimatedTimeMs = calculateEstimatedTime(metadata, preset)

        return CompressionEstimate(
            estimatedSizeBytes = estimatedBytes,
            estimatedTimeMs = estimatedTimeMs,
            originalSizeBytes = metadata.sizeBytes,
            targetVideoBitrateKbps = estimatedBitrateKbps,
            targetAudioBitrateKbps = audioBitrateKbps
        )
    }

    private fun getEstimatedBitrate(preset: CompressionPreset, metadata: VideoMetadata): Int {
        // Fallback or original
        val originalBitrateKbps = if (metadata.bitrate > 0) metadata.bitrate / 1000 else 15000

        return when (preset) {
            is CompressionPreset.MaxQuality -> (originalBitrateKbps * 0.8).toInt()
            is CompressionPreset.Balanced -> (originalBitrateKbps * 0.5).toInt()
            is CompressionPreset.SmallFile -> (originalBitrateKbps * 0.25).toInt()
            is CompressionPreset.WhatsApp -> {
                val durationS = max(1L, metadata.durationMs / 1000)
                ((16 * 8192) / durationS).toInt() - 128
            }
            is CompressionPreset.Telegram -> 3000
            is CompressionPreset.Instagram, is CompressionPreset.TikTok, is CompressionPreset.YouTubeShorts -> 5000 // typical 1080p target
            is CompressionPreset.DiscordFree -> {
                // To hit 8MB, max bitrate = (8MB * 8) / durationSeconds
                val durationS = max(1L, metadata.durationMs / 1000)
                ((8 * 8192) / durationS).toInt() - 128 // minus audio
            }
            is CompressionPreset.DiscordNitro -> {
                val durationS = max(1L, metadata.durationMs / 1000)
                ((50 * 8192) / durationS).toInt() - 128
            }
            is CompressionPreset.Email -> {
                val durationS = max(1L, metadata.durationMs / 1000)
                ((25 * 8192) / durationS).toInt() - 128
            }
            is CompressionPreset.Signal -> {
                val durationS = max(1L, metadata.durationMs / 1000)
                ((100 * 8192) / durationS).toInt() - 128
            }
            is CompressionPreset.SmartTargetSize -> {
                val durationS = max(1L, metadata.durationMs / 1000)
                ((preset.targetMb * 8192) / durationS).toInt() - 128
            }
            is CompressionPreset.Custom -> preset.customBitrateKbps ?: (originalBitrateKbps * 0.5).toInt()
        }
    }

    private fun calculateEstimatedTime(metadata: VideoMetadata, preset: CompressionPreset): Long {
        // Base is 0.5x duration
        var time = metadata.durationMs * 0.5f

        // Adjust based on codec if custom
        if (preset is CompressionPreset.Custom) {
            when (preset.codec) {
                com.example.sendready.core.domain.model.VideoCodec.H264 -> time *= 1.0f
                com.example.sendready.core.domain.model.VideoCodec.H265 -> time *= 1.5f
                com.example.sendready.core.domain.model.VideoCodec.AV1 -> time *= 3.0f
            }
        }
        
        return time.toLong()
    }
}
