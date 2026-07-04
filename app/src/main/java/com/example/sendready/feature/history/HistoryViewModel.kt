package com.example.sendready.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendready.core.domain.model.CompressionResult
import com.example.sendready.core.domain.repository.CompressionHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: CompressionHistoryRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = historyRepository.getAllHistory()
        .map { historyList ->
            var totalSaved = 0L
            for (item in historyList) {
                totalSaved += Math.max(0L, item.originalSizeBytes - item.compressedSizeBytes)
            }
            HistoryUiState(
                isLoading = false,
                items = historyList,
                totalBytesSaved = totalSaved,
                totalVideosCompressed = historyList.size
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState(isLoading = true)
        )

    fun onAction(action: HistoryUiAction) {
        when (action) {
            is HistoryUiAction.DeleteHistoryItem -> {
                viewModelScope.launch {
                    historyRepository.deleteResult(action.id)
                }
            }
            HistoryUiAction.ClearHistory -> {
                viewModelScope.launch {
                    historyRepository.clearHistory()
                }
            }
        }
    }
}
