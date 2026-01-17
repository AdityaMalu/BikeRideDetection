package com.example.bikeridedetection.ui.components.animations

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

/**
 * A composable that applies a subtle pulsing animation to its content.
 * Useful for drawing attention to active states or important elements.
 *
 * @param modifier Modifier for the container
 * @param enabled Whether the pulse animation is active
 * @param minScale Minimum scale during pulse (default: 0.95)
 * @param maxScale Maximum scale during pulse (default: 1.05)
 * @param durationMillis Duration of one pulse cycle
 * @param content The content to animate
 */
@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minScale: Float = AnimationConstants.PULSE_MIN_SCALE,
    maxScale: Float = AnimationConstants.PULSE_MAX_SCALE,
    durationMillis: Int = AnimationConstants.PULSE_DURATION,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = durationMillis),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulse_scale",
    )

    Box(
        modifier =
            modifier.scale(
                if (enabled) scale else 1f,
            ),
    ) {
        content()
    }
}

/**
 * A subtle pulse animation for status indicators.
 * Uses smaller scale values for a more refined effect.
 */
@Composable
fun SubtlePulseAnimation(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    PulseAnimation(
        modifier = modifier,
        enabled = enabled,
        minScale = 0.98f,
        maxScale = 1.02f,
        durationMillis = 1500,
        content = content,
    )
}
