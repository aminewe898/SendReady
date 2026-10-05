package com.example.sendready.feature.finished

import androidx.lifecycle.ViewModel
import com.example.sendready.core.compression.CompressionManager
import com.example.sendready.core.domain.model.CompressionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class FinishedViewModel @Inject constructor(
    private val compressionManager: CompressionManager
) : ViewModel() {
    val compressionState: StateFlow<CompressionState> = compressionManager.state
    
    fun clearState() {
        compressionManager.cancel() // Just sets state to Idle and clears resources safely
    }
}
