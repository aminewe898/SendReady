package com.example.sendready.feature.progress

import androidx.lifecycle.ViewModel
import com.example.sendready.core.compression.CompressionManager
import com.example.sendready.core.domain.model.CompressionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ProgressViewModel @Inject constructor(
    private val compressionManager: CompressionManager
) : ViewModel() {

    val compressionState: StateFlow<CompressionState> = compressionManager.state

    fun cancelCompression() {
        compressionManager.cancel()
    }
}
