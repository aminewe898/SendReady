package com.example.sendready.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sendready.core.designsystem.theme.SendReadyTheme

@Composable
fun SRButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp).fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = SendReadyTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = SendReadyTheme.colorScheme.primary,
            contentColor = SendReadyTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = SendReadyTheme.colorScheme.onPrimary,
                modifier = Modifier.height(24.dp)
            )
        } else {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.padding(end = SendReadyTheme.spacing.small)
                )
            }
            Text(
                text = text,
                style = SendReadyTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun SRSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(56.dp).fillMaxWidth(),
        enabled = enabled,
        shape = SendReadyTheme.shapes.extraLarge,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = SendReadyTheme.colorScheme.primaryContainer,
            contentColor = SendReadyTheme.colorScheme.onPrimaryContainer
        )
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.padding(end = SendReadyTheme.spacing.small)
            )
        }
        Text(
            text = text,
            style = SendReadyTheme.typography.labelLarge
        )
    }
}

@Composable
fun SROutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp).fillMaxWidth(),
        enabled = enabled,
        shape = SendReadyTheme.shapes.extraLarge
    ) {
        Text(
            text = text,
            style = SendReadyTheme.typography.labelLarge
        )
    }
}

@Composable
fun SRTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = SendReadyTheme.typography.labelLarge
        )
    }
}

@Composable
fun SRIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = SendReadyTheme.colorScheme.onSurface
        )
    }
}
