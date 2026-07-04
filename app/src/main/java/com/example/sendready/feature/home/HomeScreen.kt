package com.example.sendready.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.components.SREmptyState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

import com.example.sendready.core.navigation.UiAction
import com.example.sendready.core.navigation.UiEvent
import com.example.sendready.core.navigation.UiState

data class HomeUiState(
    val isLoading: Boolean = false,
    val selectedVideoUri: String? = null
) : UiState

sealed interface HomeUiAction : UiAction {
    data class SelectVideo(val uri: String) : HomeUiAction
}

sealed interface HomeUiEvent : UiEvent {
    data class NavigateToCompress(val uri: String) : HomeUiEvent
}

@Composable
fun HomeScreen(
    onNavigateToCompress: () -> Unit,
    modifier: Modifier = Modifier
) {
    SREmptyState(
        title = "Home",
        message = "Select a video to compress",
        icon = Icons.Default.Home,
        modifier = modifier.fillMaxSize()
    )
}
