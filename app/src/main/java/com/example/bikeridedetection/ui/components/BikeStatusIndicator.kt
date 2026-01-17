package com.example.bikeridedetection.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.bikeridedetection.R
import com.example.bikeridedetection.ui.components.animations.AnimationConstants
import com.example.bikeridedetection.ui.components.animations.SubtlePulseAnimation

private const val SCALE_ACTIVE = 1f
private const val SCALE_INACTIVE = 0.95f

/**
 * An animated status indicator that shows the current bike mode state.
 * Features smooth color transitions and a subtle pulse when active.
 *
 * @param isActive Whether bike mode is currently active
 * @param modifier Modifier for the indicator
 */
@Suppress("LongMethod")
@Composable
fun BikeStatusIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = rememberStatusColors(isActive)
    val scale by animateFloatAsState(
        targetValue = if (isActive) SCALE_ACTIVE else SCALE_INACTIVE,
        animationSpec = tween(AnimationConstants.DURATION_MEDIUM),
        label = "status_scale",
    )
    val statusText = stringResource(if (isActive) R.string.status_on else R.string.status_off)
    val accessibilityDescription =
        stringResource(
            if (isActive) R.string.cd_status_active else R.string.cd_status_inactive,
        )

    SubtlePulseAnimation(enabled = isActive) {
        StatusIndicatorContent(
            modifier = modifier,
            scale = scale,
            backgroundColor = colors.first,
            contentColor = colors.second,
            statusText = statusText,
            accessibilityDescription = accessibilityDescription,
        )
    }
}

@Composable
private fun rememberStatusColors(isActive: Boolean): Pair<Color, Color> {
    val backgroundColor by animateColorAsState(
        targetValue =
            if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        animationSpec = tween(AnimationConstants.DURATION_MEDIUM),
        label = "status_bg_color",
    )
    val contentColor by animateColorAsState(
        targetValue =
            if (isActive) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        animationSpec = tween(AnimationConstants.DURATION_MEDIUM),
        label = "status_content_color",
    )
    return backgroundColor to contentColor
}

@Composable
private fun StatusIndicatorContent(
    modifier: Modifier,
    scale: Float,
    backgroundColor: Color,
    contentColor: Color,
    statusText: String,
    accessibilityDescription: String,
) {
    Row(
        modifier =
            modifier
                .scale(scale)
                .background(color = backgroundColor, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .semantics { contentDescription = accessibilityDescription },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(8.dp)
                    .background(color = contentColor, shape = CircleShape),
        )
        Text(text = statusText, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

/**
 * An animated bike icon that responds to the active state.
 *
 * @param isActive Whether bike mode is currently active
 * @param modifier Modifier for the icon
 */
@Composable
fun AnimatedBikeIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val iconColor by animateColorAsState(
        targetValue =
            if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        animationSpec = tween(AnimationConstants.DURATION_MEDIUM),
        label = "bike_icon_color",
    )

    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = tween(AnimationConstants.DURATION_MEDIUM),
        label = "bike_icon_scale",
    )

    SubtlePulseAnimation(enabled = isActive) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = stringResource(R.string.cd_bike_icon),
            modifier =
                modifier
                    .size(80.dp)
                    .scale(scale),
            tint = iconColor,
        )
    }
}
