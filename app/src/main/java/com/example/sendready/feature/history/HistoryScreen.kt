package com.example.sendready.feature.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.components.SREmptyState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List

import com.example.sendready.core.navigation.UiAction
import com.example.sendready.core.navigation.UiEvent
import com.example.sendready.core.navigation.UiState

data class HistoryUiState(
    val isLoading: Boolean = false,
    val items: List<String> = emptyList() // Will be replaced by domain models later
) : UiState

sealed interface HistoryUiAction : UiAction {
    data class DeleteHistoryItem(val id: String) : HistoryUiAction
}

sealed interface HistoryUiEvent : UiEvent {
    data class NavigateToFinished(val videoId: String) : HistoryUiEvent
}

@Composable
fun HistoryScreen(
    onNavigateToFinished: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SREmptyState(
        title = "History",
        message = "Your past compressions will appear here",
        icon = Icons.AutoMirrored.Filled.List,
        modifier = modifier.fillMaxSize()
    )
}
