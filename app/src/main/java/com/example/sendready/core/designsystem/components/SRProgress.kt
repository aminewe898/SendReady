package com.example.sendready.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.sendready.core.designsystem.theme.SendReadyTheme

@Composable
fun SRLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = SendReadyTheme.motion.springExpressive(),
        label = "progress"
    )
    
    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        color = SendReadyTheme.colorScheme.primary,
        trackColor = SendReadyTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun SRCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = SendReadyTheme.motion.springExpressive(),
        label = "progress"
    )
    
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(120.dp),
            color = SendReadyTheme.colorScheme.primary,
            trackColor = SendReadyTheme.colorScheme.surfaceVariant,
            strokeWidth = SendReadyTheme.tokens.strokeThick,
            strokeCap = StrokeCap.Round
        )
        
        if (showText) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = SendReadyTheme.typography.titleLarge,
                color = SendReadyTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SRIndeterminateProgress(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = SendReadyTheme.colorScheme.primary,
        trackColor = SendReadyTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
    )
}
