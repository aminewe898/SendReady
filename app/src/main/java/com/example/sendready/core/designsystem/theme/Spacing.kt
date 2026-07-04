package com.example.sendready.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class SendReadySpacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 20.dp,
    val extraExtraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp,
    val extraHuge: Dp = 40.dp,
    val massive: Dp = 48.dp,
    val giant: Dp = 64.dp
)

val LocalSpacing = staticCompositionLocalOf { SendReadySpacing() }
