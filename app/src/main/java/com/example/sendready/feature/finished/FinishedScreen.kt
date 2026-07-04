package com.example.sendready.feature.finished

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.components.SREmptyState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle

import com.example.sendready.core.navigation.UiAction
import com.example.sendready.core.navigation.UiEvent
import com.example.sendready.core.navigation.UiState

data class FinishedUiState(
    val videoUri: String? = null
) : UiState

sealed interface FinishedUiAction : UiAction {
    data object ShareVideo : FinishedUiAction
}

sealed interface FinishedUiEvent : UiEvent {
    data object NavigateHome : FinishedUiEvent
}

@Composable
fun FinishedScreen(
    videoId: String,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    SREmptyState(
        title = "Finished",
        message = "Video $videoId compressed successfully",
        icon = Icons.Default.CheckCircle,
        modifier = modifier.fillMaxSize()
    )
}
