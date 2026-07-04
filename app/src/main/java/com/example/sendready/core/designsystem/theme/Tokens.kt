package com.example.sendready.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class SendReadyTokens(
    val opacityHover: Float = 0.08f,
    val opacityFocus: Float = 0.12f,
    val opacityPressed: Float = 0.12f,
    val opacityDragged: Float = 0.16f,
    val opacityDisabled: Float = 0.38f,
    val opacityScrim: Float = 0.32f,

    val strokeThin: Dp = 1.dp,
    val strokeMedium: Dp = 2.dp,
    val strokeThick: Dp = 4.dp,

    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val iconExtraLarge: Dp = 40.dp
)

val LocalTokens = staticCompositionLocalOf { SendReadyTokens() }
