package com.example.sendready.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class SendReadyMotion(
    val durationShort1: Int = 50,
    val durationShort2: Int = 100,
    val durationShort3: Int = 150,
    val durationShort4: Int = 200,
    val durationMedium1: Int = 250,
    val durationMedium2: Int = 300,
    val durationMedium3: Int = 350,
    val durationMedium4: Int = 400,
    val durationLong1: Int = 450,
    val durationLong2: Int = 500,

    val standardEasing: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f),
    val standardDecelerateEasing: Easing = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f),
    val standardAccelerateEasing: Easing = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f),
    val emphasizedEasing: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f),
    val emphasizedDecelerateEasing: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f),
    val emphasizedAccelerateEasing: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
) {
    fun <T> springExpressive() = spring<T>(
        dampingRatio = 0.7f,
        stiffness = Spring.StiffnessMediumLow
    )

    fun <T> springStandard() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

val LocalMotion = staticCompositionLocalOf { SendReadyMotion() }
