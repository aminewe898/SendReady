package com.example.sendready.core.domain.model

enum class VideoCodec(val displayName: String, val compatibilityStars: Int, val description: String) {
    H264("H.264", 5, "Universal compatibility (recommended)"),
    H265("HEVC / H.265", 4, "High compression, modern devices"),
    AV1("AV1", 2, "Experimental, highest compression")
}

enum class VideoResolution(val displayName: String, val height: Int) {
    ORIGINAL("Keep Original", -1),
    UHD_2160("2160p (4K)", 2160),
    QHD_1440("1440p (2K)", 1440),
    FHD_1080("1080p (HD)", 1080),
    HD_720("720p", 720),
    SD_480("480p", 480)
}

enum class VideoFps(val displayName: String, val value: Int) {
    ORIGINAL("Original", -1),
    FPS_60("60 fps", 60),
    FPS_30("30 fps", 30),
    FPS_24("24 fps", 24)
}

enum class AudioConfig(val displayName: String, val bitrateKbps: Int) {
    ORIGINAL("Keep Original", -1),
    KBPS_128("128 kbps", 128),
    KBPS_96("96 kbps", 96),
    KBPS_64("64 kbps", 64),
    MUTE("Mute (Remove Audio)", 0)
}

sealed class CompressionPreset(val id: String, val displayName: String) {
    // Built-in presets
    data object DiscordFree : CompressionPreset("discord_free", "Discord Free (8 MB)")
    data object DiscordNitro : CompressionPreset("discord_nitro", "Discord Nitro (50 MB)")
    data object WhatsApp : CompressionPreset("whatsapp", "WhatsApp (16 MB)")
    data object Telegram : CompressionPreset("telegram", "Telegram (2 GB)")
    data object Signal : CompressionPreset("signal", "Signal (100 MB)")
    data object Instagram : CompressionPreset("instagram", "Instagram (1080p)")
    data object TikTok : CompressionPreset("tiktok", "TikTok (1080p)")
    data object YouTubeShorts : CompressionPreset("yt_shorts", "YouTube Shorts (1080p)")
    data object Email : CompressionPreset("email", "Email (25 MB)")
    
    // Quality presets
    data object MaxQuality : CompressionPreset("max_quality", "Maximum Quality")
    data object Balanced : CompressionPreset("balanced", "Balanced")
    data object SmallFile : CompressionPreset("small_file", "Small File")
    
    // Smart Target Size (User specifies MB)
    data class SmartTargetSize(val targetMb: Int) : CompressionPreset("smart_$targetMb", "Smart Target (${targetMb}MB)")
    
    // Fully custom
    data class Custom(
        val resolution: VideoResolution = VideoResolution.ORIGINAL,
        val fps: VideoFps = VideoFps.ORIGINAL,
        val codec: VideoCodec = VideoCodec.H264,
        val audioConfig: AudioConfig = AudioConfig.ORIGINAL,
        val customBitrateKbps: Int? = null
    ) : CompressionPreset("custom", "Custom")

    companion object {
        val socialPresets: List<CompressionPreset>
            get() = listOf(
                DiscordFree, DiscordNitro, WhatsApp, Telegram, Signal, Instagram, TikTok, YouTubeShorts, Email
            )
            
        val qualityPresets: List<CompressionPreset>
            get() = listOf(
                MaxQuality, Balanced, SmallFile
            )

        val builtInPresets: List<CompressionPreset>
            get() = socialPresets + qualityPresets
    }
}
