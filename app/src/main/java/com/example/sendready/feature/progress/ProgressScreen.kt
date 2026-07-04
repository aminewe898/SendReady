package com.example.sendready.feature.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sendready.core.domain.model.CompressionState
import com.example.sendready.core.domain.model.JobStatus
import com.example.sendready.core.domain.model.JobProgress
import com.example.sendready.core.compression.toUserFriendlyMessage

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
                        text = "Compressing ${s.stats.currentFileIndex} of ${s.stats.totalFiles}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Circular overall progress
                    val animatedOverallProgress by animateFloatAsState(
                        targetValue = s.stats.overallProgressPercent / 100f,
                        label = "overall_progress"
                    )
                    
                    androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { animatedOverallProgress },
                            modifier = Modifier.fillMaxWidth(0.5f).height(192.dp),
                            strokeWidth = 16.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "${s.stats.overallProgressPercent}%",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
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
                        StatItem("ETA", String.format("%02d:%02d", mins, secs))
                        
                        StatItem("Saved", "${s.stats.bytesSavedSoFar / (1024 * 1024)} MB")
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Batch Queue List
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.stats.jobProgressList) { job ->
                            JobProgressItem(job)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
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
                        text = s.error.toUserFriendlyMessage(),
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

@Composable
private fun JobProgressItem(job: JobProgress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (job.status) {
            JobStatus.COMPLETED -> Icons.Default.Check
            JobStatus.COMPRESSING -> Icons.Default.HourglassEmpty
            JobStatus.FAILED -> Icons.Default.Error
            JobStatus.PENDING -> Icons.Default.HourglassEmpty
            JobStatus.PREPARING -> Icons.Default.HourglassEmpty
            JobStatus.CANCELED -> Icons.Default.Error
        }
        
        val tint = when (job.status) {
            JobStatus.COMPLETED -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
            JobStatus.COMPRESSING -> MaterialTheme.colorScheme.primary
            JobStatus.FAILED -> MaterialTheme.colorScheme.error
            JobStatus.CANCELED -> MaterialTheme.colorScheme.error
            JobStatus.PENDING, JobStatus.PREPARING -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        }
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.size(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = job.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (job.status == JobStatus.COMPRESSING) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { job.progressPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    strokeCap = StrokeCap.Round
                )
            } else {
                Text(
                    text = when (job.status) {
                        JobStatus.COMPLETED -> "Finished"
                        JobStatus.FAILED -> "Failed"
                        JobStatus.PENDING -> "Waiting"
                        JobStatus.PREPARING -> "Preparing"
                        JobStatus.CANCELED -> "Canceled"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (job.status == JobStatus.COMPRESSING) {
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "${job.progressPercent}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
