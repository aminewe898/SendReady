package com.example.sendready.feature.compress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendready.core.domain.model.CompressionEstimate
import com.example.sendready.core.domain.model.CompressionPreset
import com.example.sendready.core.domain.model.VideoMetadata
import com.example.sendready.core.domain.repository.MediaRepository
import com.example.sendready.core.domain.repository.VideoQueueRepository
import com.example.sendready.core.domain.usecase.EstimateCompressionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QueueItem(
    val uri: String,
    val metadata: VideoMetadata?
)

data class CompressUiState(
    val queue: List<QueueItem> = emptyList(),
    val selectedPreset: CompressionPreset = CompressionPreset.Discord8MB,
    val totalEstimate: CompressionEstimate? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class CompressViewModel @Inject constructor(
    private val videoQueueRepository: VideoQueueRepository,
    private val mediaRepository: MediaRepository,
    private val estimateCompressionUseCase: EstimateCompressionUseCase
) : ViewModel() {

    private val _selectedPreset = MutableStateFlow<CompressionPreset>(CompressionPreset.Discord8MB)
    private val _metadataCache = MutableStateFlow<Map<String, VideoMetadata>>(emptyMap())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<CompressUiState> = combine(
        videoQueueRepository.queue,
        _selectedPreset,
        _metadataCache,
        _isLoading
    ) { queueUris, preset, cache, isLoading ->
        val queueItems = queueUris.map { uri ->
            QueueItem(uri, cache[uri])
        }

        // Calculate total estimate based on loaded metadata
        val validMetadata = queueItems.mapNotNull { it.metadata }
        val totalEstimate = if (validMetadata.isNotEmpty()) {
            val estimates = validMetadata.map { estimateCompressionUseCase(it, preset) }
            CompressionEstimate(
                estimatedSizeBytes = estimates.sumOf { it.estimatedSizeBytes },
                estimatedTimeMs = estimates.sumOf { it.estimatedTimeMs },
                originalSizeBytes = estimates.sumOf { it.originalSizeBytes }
            )
        } else {
            null
        }

        CompressUiState(
            queue = queueItems,
            selectedPreset = preset,
            totalEstimate = totalEstimate,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CompressUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            videoQueueRepository.queue.collect { uris ->
                loadMetadataForUris(uris)
            }
        }
    }

    private suspend fun loadMetadataForUris(uris: List<String>) {
        val currentCache = _metadataCache.value
        val missingUris = uris.filter { !currentCache.containsKey(it) }
        
        if (missingUris.isNotEmpty()) {
            _isLoading.value = true
            val newMetadata = mutableMapOf<String, VideoMetadata>()
            for (uri in missingUris) {
                val metadata = mediaRepository.getVideoMetadata(uri)
                if (metadata != null) {
                    newMetadata[uri] = metadata
                }
            }
            _metadataCache.update { it + newMetadata }
            _isLoading.value = false
        }
    }

    fun selectPreset(preset: CompressionPreset) {
        _selectedPreset.value = preset
    }

    fun setSmartTargetMb(targetMb: Int) {
        _selectedPreset.value = CompressionPreset.SmartTargetSize(targetMb)
    }

    fun removeVideo(uri: String) {
        videoQueueRepository.removeFromQueue(uri)
    }

    fun reorderVideo(fromIndex: Int, toIndex: Int) {
        videoQueueRepository.reorderQueue(fromIndex, toIndex)
    }
}
