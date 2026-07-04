package com.example.sendready.feature.finished

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sendready.core.domain.model.CompressionState
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
    modifier: Modifier = Modifier,
    viewModel: FinishedViewModel = hiltViewModel()
) {
    val state by viewModel.compressionState.collectAsState()
    val context = LocalContext.current

    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state is CompressionState.Finished) {
                val finishedState = state as CompressionState.Finished
                val savedMb = (finishedState.totalOriginalSizeBytes - finishedState.totalCompressedSizeBytes) / (1024 * 1024)
                val originalMb = finishedState.totalOriginalSizeBytes / (1024 * 1024)
                val compressedMb = finishedState.totalCompressedSizeBytes / (1024 * 1024)
                
                val ratio = if (finishedState.totalOriginalSizeBytes > 0) {
                    100f - (finishedState.totalCompressedSizeBytes.toFloat() / finishedState.totalOriginalSizeBytes.toFloat() * 100f)
                } else 0f

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.fillMaxWidth(0.4f).height(120.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Compression Complete",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${finishedState.results.size} Videos Processed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Stats
                StatRow("Original Size", "$originalMb MB")
                StatRow("Compressed Size", "$compressedMb MB")
                StatRow("Space Saved", "$savedMb MB", highlight = true)
                StatRow("Reduction", "${ratio.toInt()}%")
                
                val timeSecs = finishedState.totalTimeMs / 1000
                StatRow("Time Elapsed", String.format("%02d:%02d", timeSecs / 60, timeSecs % 60))
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        viewModel.clearState()
                        onNavigateHome()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
                
                Text(
                    text = "Saved automatically to your device's Gallery (Movies/SendReady).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
                
                OutlinedButton(
                    onClick = {
                        if (finishedState.results.isNotEmpty()) {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND_MULTIPLE).apply {
                                type = "video/mp4"
                                val uris = finishedState.results.mapNotNull { result ->
                                    val file = java.io.File(result.outputPath)
                                    if (file.exists()) {
                                        androidx.core.content.FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file
                                        )
                                    } else null
                                }
                                putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, java.util.ArrayList(uris))
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Share Compressed Videos"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share Results")
                }
            } else {
                Text("Waiting for results...")
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
