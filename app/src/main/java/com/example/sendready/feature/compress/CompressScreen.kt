package com.example.sendready.feature.compress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sendready.core.designsystem.components.SRButton
import com.example.sendready.feature.compress.components.CompressionEstimateCard
import com.example.sendready.feature.compress.components.CompressionMeters
import com.example.sendready.feature.compress.components.PresetSelector
import com.example.sendready.feature.compress.components.VideoQueueItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompressScreen(
    onNavigateToProgress: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compression Queue") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Queue Items
                item {
                    Text(
                        text = "Videos (${uiState.queue.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(
                    items = uiState.queue,
                    key = { it.uri }
                ) { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart ||
                                dismissValue == SwipeToDismissBoxValue.StartToEnd
                            ) {
                                viewModel.removeVideo(item.uri)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = { /* Optional background for swipe */ },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        VideoQueueItem(item = item)
                    }
                }

                if (uiState.queue.isNotEmpty()) {
                    // Settings and Presets
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        PresetSelector(
                            selectedPreset = uiState.selectedPreset,
                            onPresetSelected = viewModel::selectPreset,
                            onSmartTargetMbChanged = viewModel::setSmartTargetMb
                        )
                    }

                    // Estimate Card
                    item {
                        CompressionEstimateCard(
                            estimate = uiState.totalEstimate,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Meters
                    item {
                        CompressionMeters(
                            preset = uiState.selectedPreset,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // Start Button at the bottom
            AnimatedVisibility(
                visible = uiState.queue.isNotEmpty(),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SRButton(
                    text = if (uiState.isLoading) "Loading Info..." else "Start Compression",
                    onClick = {
                        if (viewModel.startCompression()) {
                            onNavigateToProgress("active_batch")
                        }
                    },
                    enabled = !uiState.isLoading && uiState.queue.all { it.metadata != null },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

