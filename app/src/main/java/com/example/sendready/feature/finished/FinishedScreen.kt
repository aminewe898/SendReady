package com.example.sendready.feature.finished

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import kotlin.math.roundToInt
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

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Compression Complete 🎉",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${finishedState.results.size} Videos Processed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (finishedState.results.size == 1) {
                    val result = finishedState.results.first()
                    BeforeAfterSlider(
                        originalUri = result.originalUri,
                        compressedPath = result.outputPath,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Original -> Compressed Row
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Original", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$originalMb MB", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "to",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Compressed", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$compressedMb MB", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Saved", style = MaterialTheme.typography.labelMedium)
                                Text("$savedMb MB", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Reduction", style = MaterialTheme.typography.labelMedium)
                                Text("${ratio.toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            val timeSecs = finishedState.totalTimeMs / 1000
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Time", style = MaterialTheme.typography.labelMedium)
                                Text(String.format("%02d:%02d", timeSecs / 60, timeSecs % 60), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Saved automatically to your device's Gallery (Movies/SendReady).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Actions Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
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
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            // TODO: Open intent
                        },
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // TODO: Delete original logic
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete")
                    }

                    Button(
                        onClick = {
                            viewModel.clearState()
                            onNavigateHome()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("Compress Another")
                    }
                }
            } else {
                Text("Waiting for results...")
            }
        }
    }
}

@Composable
fun BeforeAfterSlider(
    originalUri: String,
    compressedPath: String,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableStateOf(0.5f) }
    var sliderWidth by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    
    val imageLoader = androidx.compose.runtime.remember {
        ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    Box(
        modifier = modifier
            .onSizeChanged { sliderWidth = it.width }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newPosition = sliderPosition + (dragAmount.x / size.width)
                    sliderPosition = newPosition.coerceIn(0f, 1f)
                }
            }
    ) {
        // Bottom image (Original)
        AsyncImage(
            model = originalUri,
            imageLoader = imageLoader,
            contentDescription = "Original Video Frame",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top image (Compressed), clipped
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    object : androidx.compose.ui.graphics.Shape {
                        override fun createOutline(
                            size: androidx.compose.ui.geometry.Size,
                            layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                            density: androidx.compose.ui.unit.Density
                        ): androidx.compose.ui.graphics.Outline {
                            return androidx.compose.ui.graphics.Outline.Rectangle(
                                androidx.compose.ui.geometry.Rect(
                                    0f, 0f, size.width * sliderPosition, size.height
                                )
                            )
                        }
                    }
                )
        ) {
            AsyncImage(
                model = compressedPath,
                imageLoader = imageLoader,
                contentDescription = "Compressed Video Frame",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Slider line and thumb
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val thumbWidth = 4.dp
            Box(
                modifier = Modifier
                    .width(thumbWidth)
                    .fillMaxSize()
                    .background(Color.White)
                    .align(Alignment.CenterStart)
                    .offset {
                        IntOffset((sliderWidth * sliderPosition - thumbWidth.toPx() / 2).roundToInt(), 0)
                    }
            )
            
            // Labels
            Text(
                "Compressed",
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(Color.Black.copy(alpha=0.5f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp),
                color = Color.White, style = MaterialTheme.typography.labelSmall
            )
            Text(
                "Original",
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).background(Color.Black.copy(alpha=0.5f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp),
                color = Color.White, style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

