package com.example.sendready.core.compression

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.TransformationRequest
import com.example.sendready.core.domain.model.CompressionJob
import com.example.sendready.core.domain.model.CompressionPreset
import com.example.sendready.core.domain.model.CompressionResult
import com.example.sendready.core.domain.model.CompressionState
import com.example.sendready.core.domain.model.VideoCodec
import com.example.sendready.core.domain.model.VideoResolution
import com.example.sendready.core.domain.repository.CompressionHistoryRepository
import com.google.common.collect.ImmutableList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class CompressionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val historyRepository: CompressionHistoryRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var compressionJob: Job? = null
    private var progressJob: Job? = null

    private val _state = MutableStateFlow<CompressionState>(CompressionState.Idle)
    val state: StateFlow<CompressionState> = _state.asStateFlow()

    private val progressTracker = CompressionProgressTracker()
    private var activeTransformer: Transformer? = null
    
    // Callback to let the service know when we're completely finished
    var onQueueFinished: (() -> Unit)? = null

    fun start(jobs: List<CompressionJob>) {
        if (_state.value is CompressionState.Compressing || jobs.isEmpty()) return

        // Start Foreground Service
        val intent = android.content.Intent(context, CompressionForegroundService::class.java).apply {
            action = CompressionForegroundService.ACTION_START
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        progressTracker.startTracking(jobs)
        _state.value = CompressionState.Compressing(progressTracker.getStats(jobs.first().id))

        compressionJob = scope.launch {
            val results = mutableListOf<CompressionResult>()
            var totalOriginalBytes = 0L
            var totalCompressedBytes = 0L
            val queueStartTime = System.currentTimeMillis()

            try {
                for (job in jobs) {
                    val currentStats = progressTracker.getStats(job.id)
                    _state.value = CompressionState.Compressing(currentStats)
                    
                    val result = processSingleJob(job)
                    
                    results.add(result)
                    totalOriginalBytes += job.metadata.sizeBytes
                    totalCompressedBytes += result.compressedSizeBytes
                    progressTracker.addBytesSaved(job.metadata.sizeBytes - result.compressedSizeBytes)
                    
                    // Mark this job as 100% complete in the tracker
                    progressTracker.updateProgress(job.id, 100)
                }

                val totalTimeMs = System.currentTimeMillis() - queueStartTime
                _state.value = CompressionState.Finished(
                    results = results,
                    totalOriginalSizeBytes = totalOriginalBytes,
                    totalCompressedSizeBytes = totalCompressedBytes,
                    totalTimeMs = totalTimeMs
                )
            } catch (e: Exception) {
                Log.e("CompressionManager", "Compression failed or was cancelled", e)
                if (e is kotlinx.coroutines.CancellationException) {
                    _state.value = CompressionState.Idle
                } else {
                    _state.value = CompressionState.Failed(e, results)
                }
            } finally {
                activeTransformer?.cancel()
                activeTransformer = null
                stopProgressPolling()
                onQueueFinished?.invoke()
            }
        }
    }

    fun cancel() {
        compressionJob?.cancel()
        activeTransformer?.cancel()
        activeTransformer = null
        _state.value = CompressionState.Idle
    }

    private suspend fun processSingleJob(job: CompressionJob): CompressionResult = withContext(Dispatchers.Main) {
        return@withContext kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            Log.d("CompressionManager", "Starting processSingleJob for job: ${job.id}")
            val outputPath = getOutputFilePath(job.metadata.filename)
            Log.d("CompressionManager", "Created output file path: $outputPath")
            
            Log.d("CompressionManager", "Opening input URI: ${job.metadata.uriString}")
            val mediaItem = MediaItem.fromUri(Uri.parse(job.metadata.uriString))
            
            // Map preset to Transformer settings
            val targetVideoMimeType = when (job.preset) {
                is CompressionPreset.Custom -> {
                    if (job.preset.codec == VideoCodec.H265) MimeTypes.VIDEO_H265 else MimeTypes.VIDEO_H264
                }
                else -> MimeTypes.VIDEO_H264 // Default to H264 for compatibility
            }

            // Handle resolution
            val targetResolution = when (val preset = job.preset) {
                is CompressionPreset.Custom -> {
                    if (preset.resolution != VideoResolution.ORIGINAL) preset.resolution.height else null
                }
                is CompressionPreset.DiscordFree, is CompressionPreset.DiscordNitro, is CompressionPreset.SmallFile, is CompressionPreset.WhatsApp, is CompressionPreset.Telegram, is CompressionPreset.Signal, is CompressionPreset.Email -> {
                    720 // Cap at 720p for messaging/small presets
                }
                is CompressionPreset.Instagram, is CompressionPreset.TikTok, is CompressionPreset.YouTubeShorts -> {
                    1080 // Cap at 1080p for social media presets
                }
                is CompressionPreset.MaxQuality -> {
                    null // Keep original
                }
                else -> {
                    1080 // Balanced default
                }
            }

            Log.d("CompressionManager", "Building Effects with targetResolution=$targetResolution")
            val effects = if (targetResolution != null) {
                val presentation = androidx.media3.effect.Presentation.createForHeight(targetResolution)
                androidx.media3.transformer.Effects(
                    emptyList(),
                    listOf<androidx.media3.common.Effect>(presentation)
                )
            } else {
                androidx.media3.transformer.Effects(
                    emptyList(),
                    emptyList<androidx.media3.common.Effect>()
                )
            }

            Log.d("CompressionManager", "Building EditedMediaItem")
            val editedMediaItem = EditedMediaItem.Builder(mediaItem)
                .setEffects(effects)
                .build()

            val encoderFactory = androidx.media3.transformer.DefaultEncoderFactory.Builder(context).apply {
                if (job.estimate?.targetVideoBitrateKbps != null) {
                    setRequestedVideoEncoderSettings(
                        androidx.media3.transformer.VideoEncoderSettings.Builder()
                            .setBitrate(job.estimate.targetVideoBitrateKbps * 1000)
                            .build()
                    )
                }
                if (job.estimate?.targetAudioBitrateKbps != null) {
                    setRequestedAudioEncoderSettings(
                        androidx.media3.transformer.AudioEncoderSettings.Builder()
                            .setBitrate(job.estimate.targetAudioBitrateKbps * 1000)
                            .build()
                    )
                }
            }.build()

            Log.d("CompressionManager", "Building Transformer")
            val transformer = Transformer.Builder(context)
                .setVideoMimeType(targetVideoMimeType)
                .setEncoderFactory(encoderFactory)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        Log.d("CompressionManager", "Transformer completed successfully for job: ${job.id}")
                        val outputFile = File(outputPath)
                        val originalSize = job.metadata.sizeBytes
                        val compressedSize = outputFile.length()
                        
                        val result = CompressionResult(
                            id = UUID.randomUUID().toString(),
                            dateMs = System.currentTimeMillis(),
                            filename = outputFile.name,
                            originalSizeBytes = originalSize,
                            compressedSizeBytes = compressedSize,
                            compressionRatio = if (originalSize > 0) compressedSize.toFloat() / originalSize.toFloat() else 0f,
                            presetId = job.preset.id,
                            codec = exportResult.videoEncoderName ?: "Unknown",
                            durationMs = job.metadata.durationMs,
                            outputPath = outputPath,
                            originalUri = job.metadata.uriString,
                            thumbnailUri = null
                        )
                        
                        // Save to history DB
                        scope.launch {
                            historyRepository.saveResult(result)
                        }

                        // Make the file visible in the Android Gallery immediately
                        android.media.MediaScannerConnection.scanFile(
                            context,
                            arrayOf(outputPath),
                            arrayOf("video/mp4")
                        ) { path, uri ->
                            Log.d("CompressionManager", "Scanned $path: -> uri=$uri")
                        }
                        
                        if (continuation.isActive) {
                            continuation.resume(result) { }
                        }
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        Log.e("CompressionManager", "Transformer error for job: ${job.id}", exportException)
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.failure(exportException))
                        }
                    }
                })
                .build()

            activeTransformer = transformer

            // Start polling progress
            Log.d("CompressionManager", "Starting progress polling for job: ${job.id}")
            startProgressPolling(job.id, transformer)

            Log.d("CompressionManager", "Calling Transformer.start")
            transformer.start(editedMediaItem, outputPath)
            Log.d("CompressionManager", "Transformer.start called successfully")

            continuation.invokeOnCancellation {
                Log.d("CompressionManager", "Job cancelled, cancelling Transformer: ${job.id}")
                transformer.cancel()
            }
        }
    }

    private fun startProgressPolling(jobId: String, transformer: Transformer) {
        progressJob?.cancel()
        progressJob = scope.launch {
            val progressHolder = androidx.media3.transformer.ProgressHolder()
            while (true) {
                val progressState = transformer.getProgress(progressHolder)
                if (progressState == Transformer.PROGRESS_STATE_AVAILABLE) {
                    val percent = progressHolder.progress
                    Log.d("CompressionManager", "Progress available for job $jobId: $percent%")
                    progressTracker.updateProgress(jobId, percent)
                    _state.value = CompressionState.Compressing(progressTracker.getStats(jobId))
                } else if (progressState == Transformer.PROGRESS_STATE_UNAVAILABLE) {
                    Log.d("CompressionManager", "Progress unavailable for job $jobId, but still running")
                    // Progress unavailable, but still running
                } else if (progressState == Transformer.PROGRESS_STATE_NOT_STARTED) {
                    Log.d("CompressionManager", "Progress state: NOT_STARTED for job $jobId")
                    break
                }
                delay(500) // Poll every 500ms
            }
        }
    }

    private fun stopProgressPolling() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun getOutputFilePath(originalName: String): String {
        val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val sendReadyDir = File(moviesDir, "SendReady")
        if (!sendReadyDir.exists()) {
            sendReadyDir.mkdirs()
        }

        val baseName = originalName.substringBeforeLast(".")
        val extension = ".mp4" // Transformer generally outputs mp4
        
        var outputFile = File(sendReadyDir, "$baseName$extension")
        var counter = 1
        
        while (outputFile.exists()) {
            outputFile = File(sendReadyDir, "$baseName ($counter)$extension")
            counter++
        }
        
        return outputFile.absolutePath
    }
}
