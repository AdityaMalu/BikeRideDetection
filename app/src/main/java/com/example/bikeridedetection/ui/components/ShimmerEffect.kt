package com.example.bikeridedetection.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val SKELETON_SETTINGS_ITEM_COUNT = 3

/**
 * Creates a shimmer effect brush for loading placeholders.
 */
@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    targetValue: Float = 1000f,
): Brush =
    if (showShimmer) {
        val shimmerColors =
            listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            )
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmer_translate",
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation),
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero,
        )
    }

/**
 * A shimmer placeholder box.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 16.dp,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp),
) {
    Box(
        modifier =
            modifier
                .width(width)
                .height(height)
                .clip(shape)
                .background(shimmerBrush()),
    )
}

/**
 * A circular shimmer placeholder.
 */
@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(shimmerBrush()),
    )
}

/**
 * A skeleton card that mimics the main hero card during loading.
 */
@Composable
fun SkeletonHeroCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShimmerCircle(size = 80.dp)
            Spacer(modifier = Modifier.height(16.dp))
            ShimmerBox(width = 120.dp, height = 24.dp)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerBox(width = 80.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(24.dp))
            ShimmerBox(width = 200.dp, height = 48.dp, shape = RoundedCornerShape(24.dp))
        }
    }
}

/**
 * A skeleton for a settings toggle item.
 */
@Composable
fun SkeletonSettingsItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerCircle(size = 24.dp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(width = 120.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(width = 180.dp, height = 12.dp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        ShimmerBox(width = 48.dp, height = 24.dp, shape = RoundedCornerShape(12.dp))
    }
}

/**
 * A skeleton screen for the settings page.
 */
@Composable
fun SkeletonSettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        ShimmerBox(width = 100.dp, height = 20.dp)
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(), width = 0.dp, height = 80.dp, shape = RoundedCornerShape(8.dp))
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerBox(width = 80.dp, height = 20.dp)
        Spacer(modifier = Modifier.height(8.dp))
        repeat(SKELETON_SETTINGS_ITEM_COUNT) {
            SkeletonSettingsItem()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
