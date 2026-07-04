package com.example.sendready.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.theme.SendReadyTheme

@Composable
fun SRSnackBar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier.padding(SendReadyTheme.spacing.medium),
        containerColor = SendReadyTheme.colorScheme.inverseSurface,
        contentColor = SendReadyTheme.colorScheme.inverseOnSurface,
        actionColor = SendReadyTheme.colorScheme.inversePrimary,
        shape = SendReadyTheme.shapes.extraSmall
    )
}
