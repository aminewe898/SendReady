package com.example.sendready.core.domain.usecase

import com.example.sendready.core.domain.model.CompressionPreset
import com.example.sendready.core.domain.model.VideoMetadata
import javax.inject.Inject

class GetSmartRecommendationUseCase @Inject constructor() {
    
    operator fun invoke(metadataList: List<VideoMetadata>, preset: CompressionPreset): String? {
        if (metadataList.isEmpty()) return null

        val totalOriginalSizeBytes = metadataList.sumOf { it.sizeBytes }
        val averageBitrate = metadataList.map { it.bitrate }.average()
        val has4K = metadataList.any { it.height >= 2160 || it.width >= 2160 }

        // Highly compressed already check
        if (averageBitrate > 0 && averageBitrate < 1500 * 1000) { // < 1.5 Mbps
            return "These videos are already highly compressed. Further compression may reduce quality without saving much space."
        }

        // Extreme compression warning
        when (preset) {
            is CompressionPreset.DiscordFree -> {
                if (totalOriginalSizeBytes > 500L * 1024 * 1024) {
                    return "Warning: Squeezing very large videos into 8MB will result in significant quality loss."
                }
            }
            is CompressionPreset.DiscordNitro -> {
                if (totalOriginalSizeBytes > 2000L * 1024 * 1024) { // 2 GB
                    return "Warning: Squeezing over 2GB into 50MB may cause noticeable quality loss."
                }
            }
            is CompressionPreset.SmartTargetSize -> {
                // If they ask for < 5MB for a huge video
                if (preset.targetMb < 10 && totalOriginalSizeBytes > 200L * 1024 * 1024) {
                    return "Warning: Your target size is very small compared to the original video size."
                }
            }
            else -> {}
        }
        
        // 4K tip
        if (has4K && (preset is CompressionPreset.DiscordFree || preset is CompressionPreset.WhatsApp || preset is CompressionPreset.Email)) {
            return "Tip: 4K videos will be scaled down to 720p to fit the strict file size limits of this preset."
        }

        return null
    }
}
