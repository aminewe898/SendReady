package com.example.sendready.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sendready.core.designsystem.components.SRCard
import com.example.sendready.core.designsystem.theme.SendReadyTheme
import com.example.sendready.core.navigation.UiAction
import com.example.sendready.core.navigation.UiEvent
import com.example.sendready.core.navigation.UiState

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
) : UiState

sealed interface SettingsUiAction : UiAction {
    data class ToggleDarkMode(val enabled: Boolean) : SettingsUiAction
}

sealed interface SettingsUiEvent : UiEvent

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().padding(SendReadyTheme.spacing.large)) {
        Text(
            text = "Settings",
            style = SendReadyTheme.typography.headlineMedium,
            color = SendReadyTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.padding(SendReadyTheme.spacing.medium))

        SRCard {
            Row(
                modifier = Modifier.fillMaxWidth().padding(SendReadyTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = SendReadyTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(SendReadyTheme.spacing.medium))
                Text(
                    text = "Dark Mode",
                    style = SendReadyTheme.typography.bodyLarge,
                    color = SendReadyTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.isDarkMode,
                    onCheckedChange = { isChecked ->
                        viewModel.onAction(SettingsUiAction.ToggleDarkMode(isChecked))
                    }
                )
            }
        }
    }
}
