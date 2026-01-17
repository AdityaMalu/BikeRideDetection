package com.example.bikeridedetection.ui.components.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates a shimmer brush for loading placeholder animations.
 *
 * @param showShimmer Whether to show the shimmer effect
 * @param baseColor Base color for the shimmer
 * @param highlightColor Highlight color for the shimmer
 * @return A brush that creates the shimmer effect
 */
@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface,
): Brush {
    if (!showShimmer) {
        return Brush.linearGradient(
            colors = listOf(baseColor, baseColor),
            start = Offset.Zero,
            end = Offset.Zero,
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = AnimationConstants.SHIMMER_DURATION,
                        easing = LinearEasing,
                    ),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer_progress",
    )

    val shimmerColors =
        listOf(
            baseColor,
            highlightColor,
            baseColor,
        )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = shimmerProgress * 1000f - 500f, y = 0f),
        end = Offset(x = shimmerProgress * 1000f, y = 0f),
    )
}

/**
 * A shimmer placeholder box for loading states.
 *
 * @param modifier Modifier for the placeholder
 * @param height Height of the placeholder
 * @param cornerRadius Corner radius of the placeholder
 */
@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    cornerRadius: Dp = 8.dp,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shimmerBrush()),
    )
}

/**
 * A circular shimmer placeholder for avatars or icons.
 *
 * @param modifier Modifier for the placeholder
 * @param size Size of the circular placeholder
 */
@Composable
fun CircularShimmerPlaceholder(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    Box(
        modifier =
            modifier
                .height(size)
                .clip(RoundedCornerShape(size / 2))
                .background(shimmerBrush()),
    )
}
