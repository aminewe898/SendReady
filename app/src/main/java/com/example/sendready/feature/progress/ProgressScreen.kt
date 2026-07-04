package com.example.sendready.feature.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sendready.core.domain.model.CompressionState

@Composable
fun ProgressScreen(
    destinationId: String,
    onNavigateToFinished: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.compressionState.collectAsState()

    LaunchedEffect(state) {
        if (state is CompressionState.Finished) {
            onNavigateToFinished("batch_done")
        } else if (state is CompressionState.Failed) {
            // Handle error, maybe navigate back or show a retry
        } else if (state is CompressionState.Idle) {
            // Was cancelled or not started
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val s = state) {
                is CompressionState.Compressing -> {
                    Text(
                        text = "Compressing",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Video ${s.stats.currentFileIndex} of ${s.stats.totalFiles}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Circular overall progress
                    val animatedOverallProgress by animateFloatAsState(
                        targetValue = s.stats.overallProgressPercent / 100f,
                        label = "overall_progress"
                    )
                    
                    CircularProgressIndicator(
                        progress = { animatedOverallProgress },
                        modifier = Modifier.fillMaxWidth(0.5f).height(192.dp),
                        strokeWidth = 16.dp,
                        strokeCap = StrokeCap.Round
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${s.stats.overallProgressPercent}%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Current File Progress
                    val animatedFileProgress by animateFloatAsState(
                        targetValue = s.stats.currentFileProgressPercent / 100f,
                        label = "file_progress"
                    )
                    
                    Text(
                        text = "Current: ${s.stats.currentFileName}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedFileProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        strokeCap = StrokeCap.Round
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stats grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("Speed", formatSpeed(s.stats.speedBytesPerSecond))
                        
                        val remainingSeconds = s.stats.estimatedRemainingTimeMs / 1000
                        val mins = remainingSeconds / 60
                        val secs = remainingSeconds % 60
                        StatItem("Remaining", String.format("%02d:%02d", mins, secs))
                        
                        StatItem("Saved", "${s.stats.bytesSavedSoFar / (1024 * 1024)} MB")
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { viewModel.cancelCompression() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
                is CompressionState.Failed -> {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Compression Failed",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = s.error.message ?: "Unknown error occurred.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { viewModel.cancelCompression() }) {
                        Text("Dismiss")
                    }
                }
                else -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Preparing...")
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatSpeed(bytesPerSec: Long): String {
    if (bytesPerSec == 0L) return "-- MB/s"
    val mbPerSec = bytesPerSec.toFloat() / (1024f * 1024f)
    return String.format("%.1f MB/s", mbPerSec)
}
