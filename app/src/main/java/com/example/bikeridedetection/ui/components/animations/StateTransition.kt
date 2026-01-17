package com.example.bikeridedetection.ui.components.animations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Animates a color transition between two states.
 *
 * @param targetColor The target color to animate to
 * @param durationMillis Duration of the animation
 * @param label Label for the animation (for debugging)
 * @return Animated color state
 */
@Composable
fun animateStateColor(
    targetColor: Color,
    durationMillis: Int = AnimationConstants.DURATION_MEDIUM,
    label: String = "color_transition",
): State<Color> =
    animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis),
        label = label,
    )

/**
 * Animates a Dp value transition between two states.
 *
 * @param targetValue The target Dp value to animate to
 * @param durationMillis Duration of the animation
 * @param label Label for the animation (for debugging)
 * @return Animated Dp state
 */
@Composable
fun animateStateDp(
    targetValue: Dp,
    durationMillis: Int = AnimationConstants.DURATION_MEDIUM,
    label: String = "dp_transition",
): State<Dp> =
    animateDpAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis),
        label = label,
    )

/**
 * Animates a float value transition between two states.
 *
 * @param targetValue The target float value to animate to
 * @param durationMillis Duration of the animation
 * @param label Label for the animation (for debugging)
 * @return Animated float state
 */
@Composable
fun animateStateFloat(
    targetValue: Float,
    durationMillis: Int = AnimationConstants.DURATION_MEDIUM,
    label: String = "float_transition",
): State<Float> =
    animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis),
        label = label,
    )

/**
 * Animates alpha based on enabled state.
 *
 * @param enabled Whether the element is enabled
 * @param enabledAlpha Alpha when enabled
 * @param disabledAlpha Alpha when disabled
 * @return Animated alpha state
 */
@Composable
fun animateEnabledAlpha(
    enabled: Boolean,
    enabledAlpha: Float = 1f,
    disabledAlpha: Float = 0.38f,
): State<Float> =
    animateStateFloat(
        targetValue = if (enabled) enabledAlpha else disabledAlpha,
        label = "enabled_alpha",
    )

/**
 * Animates scale based on pressed state.
 *
 * @param pressed Whether the element is pressed
 * @return Animated scale state
 */
@Composable
fun animatePressedScale(pressed: Boolean): State<Float> =
    animateStateFloat(
        targetValue =
            if (pressed) {
                AnimationConstants.SCALE_PRESSED
            } else {
                AnimationConstants.SCALE_NORMAL
            },
        durationMillis = AnimationConstants.DURATION_SHORT,
        label = "pressed_scale",
    )
