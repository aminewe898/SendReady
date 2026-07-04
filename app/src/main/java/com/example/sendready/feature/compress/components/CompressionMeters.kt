package com.example.sendready.feature.compress.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sendready.core.domain.model.CompressionPreset

@Composable
fun CompressionMeters(
    preset: CompressionPreset,
    modifier: Modifier = Modifier
) {
    val qualityValue = when (preset) {
        is CompressionPreset.MaxQuality -> 0.95f
        is CompressionPreset.Balanced -> 0.7f
        is CompressionPreset.SmallFile -> 0.4f
        is CompressionPreset.Discord8MB, is CompressionPreset.SmartTargetSize -> 0.5f // Variable
        is CompressionPreset.Discord50MB -> 0.75f
        is CompressionPreset.WhatsApp, is CompressionPreset.Telegram -> 0.6f
        is CompressionPreset.Gmail -> 0.7f
        is CompressionPreset.Custom -> 0.8f
    }

    val codec = if (preset is CompressionPreset.Custom) preset.codec else com.example.sendready.core.domain.model.VideoCodec.H264
    
    val speedValue = when (codec) {
        com.example.sendready.core.domain.model.VideoCodec.H264 -> 0.9f
        com.example.sendready.core.domain.model.VideoCodec.H265 -> 0.6f
        com.example.sendready.core.domain.model.VideoCodec.AV1 -> 0.2f
    }

    val compatibilityValue = codec.compatibilityStars / 5f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MeterBar(
            label = "Quality",
            value = qualityValue,
            color = MaterialTheme.colorScheme.primary
        )
        MeterBar(
            label = "Speed",
            value = speedValue,
            color = MaterialTheme.colorScheme.tertiary
        )
        MeterBar(
            label = "Compatibility",
            value = compatibilityValue,
            color = Color(0xFF4CAF50) // Green
        )
    }
}

@Composable
private fun MeterBar(
    label: String,
    value: Float,
    color: Color
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "meter_animation"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${(animatedValue * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedValue)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}
