package com.example.sendready.feature.compress

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.components.SREmptyState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build

import com.example.sendready.core.navigation.UiAction
import com.example.sendready.core.navigation.UiEvent
import com.example.sendready.core.navigation.UiState

data class CompressUiState(
    val isLoading: Boolean = false
) : UiState

sealed interface CompressUiAction : UiAction {
    data object StartCompression : CompressUiAction
}

sealed interface CompressUiEvent : UiEvent {
    data class NavigateToProgress(val destinationId: String) : CompressUiEvent
}

@Composable
fun CompressScreen(
    onNavigateToProgress: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    SREmptyState(
        title = "Compress",
        message = "Compression options will go here",
        icon = Icons.Default.Build,
        modifier = modifier.fillMaxSize()
    )
}
