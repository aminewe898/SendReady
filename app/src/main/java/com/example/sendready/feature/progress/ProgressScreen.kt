package com.example.sendready.feature.progress

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.components.SREmptyState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh

import com.example.sendready.core.navigation.UiAction
import com.example.sendready.core.navigation.UiEvent
import com.example.sendready.core.navigation.UiState

data class ProgressUiState(
    val progress: Float = 0f,
    val isFinished: Boolean = false
) : UiState

sealed interface ProgressUiAction : UiAction {
    data object CancelCompression : ProgressUiAction
}

sealed interface ProgressUiEvent : UiEvent {
    data class NavigateToFinished(val videoId: String) : ProgressUiEvent
}

@Composable
fun ProgressScreen(
    destinationId: String,
    onNavigateToFinished: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SREmptyState(
        title = "Compressing...",
        message = "Target: $destinationId",
        icon = Icons.Default.Refresh,
        modifier = modifier.fillMaxSize()
    )
}
