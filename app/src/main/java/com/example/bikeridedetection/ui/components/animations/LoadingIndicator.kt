package com.example.bikeridedetection.ui.components.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A customizable loading indicator with smooth rotation animation.
 * Follows Material Design 3 guidelines for progress indicators.
 *
 * @param modifier Modifier for the loading indicator
 * @param size Size of the indicator
 * @param color Color of the indicator (defaults to primary)
 * @param strokeWidth Width of the circular stroke
 * @param contentDescription Accessibility description
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = AnimationConstants.LOADING_STROKE_WIDTH.dp,
    contentDescription: String = "Loading",
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = AnimationConstants.ROTATION_FULL,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = AnimationConstants.LOADING_ROTATION_DURATION,
                        easing = LinearEasing,
                    ),
                repeatMode = RepeatMode.Restart,
            ),
        label = "rotation",
    )

    Box(
        modifier =
            modifier
                .size(size)
                .rotate(rotation)
                .semantics {
                    this.contentDescription = contentDescription
                },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = color,
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round,
        )
    }
}

/**
 * A small inline loading indicator for buttons and compact spaces.
 */
@Composable
fun SmallLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
) {
    LoadingIndicator(
        modifier = modifier,
        size = 20.dp,
        color = color,
        strokeWidth = 2.dp,
        contentDescription = "Loading",
    )
}

/**
 * A large loading indicator for full-screen loading states.
 */
@Composable
fun LargeLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    LoadingIndicator(
        modifier = modifier,
        size = 64.dp,
        color = color,
        strokeWidth = 6.dp,
        contentDescription = "Loading content",
    )
}
