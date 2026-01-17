package com.example.bikeridedetection.ui.components.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A fade-in/fade-out transition wrapper for content visibility changes.
 *
 * @param visible Whether the content should be visible
 * @param modifier Modifier for the container
 * @param enterDuration Duration of the enter animation
 * @param exitDuration Duration of the exit animation
 * @param content The content to animate
 */
@Composable
fun FadeTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enterDuration: Int = AnimationConstants.FADE_IN_DURATION,
    exitDuration: Int = AnimationConstants.FADE_OUT_DURATION,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(enterDuration)),
        exit = fadeOut(animationSpec = tween(exitDuration)),
    ) {
        content()
    }
}

/**
 * A scale and fade transition for more prominent visibility changes.
 */
@Composable
fun ScaleFadeTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationConstants.DURATION_MEDIUM,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter =
            fadeIn(animationSpec = tween(durationMillis)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(durationMillis),
                ),
        exit =
            fadeOut(animationSpec = tween(durationMillis)) +
                scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(durationMillis),
                ),
    ) {
        content()
    }
}

/**
 * A slide and fade transition for content appearing from bottom.
 */
@Composable
fun SlideUpFadeTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationConstants.DURATION_MEDIUM,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter =
            fadeIn(animationSpec = tween(durationMillis)) +
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(durationMillis),
                ),
        exit =
            fadeOut(animationSpec = tween(durationMillis)) +
                slideOutVertically(
                    targetOffsetY = { it / 4 },
                    animationSpec = tween(durationMillis),
                ),
    ) {
        content()
    }
}

/**
 * Predefined enter transitions for common use cases.
 */
object EnterTransitions {
    val fadeIn: EnterTransition = fadeIn(tween(AnimationConstants.DURATION_MEDIUM))
    val scaleIn: EnterTransition =
        scaleIn(initialScale = 0.9f, animationSpec = tween(AnimationConstants.DURATION_MEDIUM))
    val slideUp: EnterTransition =
        slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(AnimationConstants.DURATION_MEDIUM),
        )
}

/**
 * Predefined exit transitions for common use cases.
 */
object ExitTransitions {
    val fadeOut: ExitTransition = fadeOut(tween(AnimationConstants.DURATION_SHORT))
    val scaleOut: ExitTransition =
        scaleOut(targetScale = 0.9f, animationSpec = tween(AnimationConstants.DURATION_SHORT))
    val slideDown: ExitTransition =
        slideOutVertically(
            targetOffsetY = { it / 3 },
            animationSpec = tween(AnimationConstants.DURATION_SHORT),
        )
}
