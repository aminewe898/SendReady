package com.example.sendready.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.theme.SendReadyTheme

@Composable
fun SRCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = SendReadyTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = SendReadyTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(SendReadyTheme.spacing.medium)) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier,
            shape = SendReadyTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = SendReadyTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(SendReadyTheme.spacing.medium)) {
                content()
            }
        }
    }
}

@Composable
fun SRElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier,
            shape = SendReadyTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(
                containerColor = SendReadyTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(modifier = Modifier.padding(SendReadyTheme.spacing.large)) {
                content()
            }
        }
    } else {
        ElevatedCard(
            modifier = modifier,
            shape = SendReadyTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(
                containerColor = SendReadyTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(modifier = Modifier.padding(SendReadyTheme.spacing.large)) {
                content()
            }
        }
    }
}
