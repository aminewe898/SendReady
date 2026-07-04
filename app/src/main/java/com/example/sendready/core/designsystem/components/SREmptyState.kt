package com.example.sendready.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sendready.core.designsystem.theme.SendReadyTheme

@Composable
fun SREmptyState(
    title: String,
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SendReadyTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = SendReadyTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(SendReadyTheme.spacing.large))
        
        Text(
            text = title,
            style = SendReadyTheme.typography.headlineSmall,
            color = SendReadyTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(SendReadyTheme.spacing.small))
        
        Text(
            text = message,
            style = SendReadyTheme.typography.bodyMedium,
            color = SendReadyTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (action != null) {
            Spacer(modifier = Modifier.height(SendReadyTheme.spacing.extraLarge))
            action()
        }
    }
}
