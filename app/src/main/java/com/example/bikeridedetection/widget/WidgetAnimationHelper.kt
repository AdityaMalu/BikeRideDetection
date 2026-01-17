package com.example.bikeridedetection.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import com.example.bikeridedetection.R
import kotlinx.coroutines.delay

/**
 * Helper class for animating widget state transitions.
 * Uses multi-step RemoteViews updates to simulate smooth animations
 * within the limitations of the RemoteViews API.
 */
object WidgetAnimationHelper {
    // Animation configuration
    private const val ANIMATION_DURATION_MS = 250L
    private const val ANIMATION_STEPS = 8
    private const val STEP_DELAY_MS = ANIMATION_DURATION_MS / ANIMATION_STEPS

    // Toggle thumb positions in dp
    private const val THUMB_MARGIN_OFF_DP = 2f
    private const val THUMB_MARGIN_ON_DP = 26f

    /**
     * Animates the toggle switch thumb from one position to another.
     * Uses incremental margin updates to create a sliding effect.
     *
     * @param context Application context
     * @param appWidgetManager Widget manager instance
     * @param appWidgetId Widget ID to update
     * @param toEnabled Target state (true = ON position, false = OFF position)
     */
    suspend fun animateToggleThumb(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        toEnabled: Boolean,
    ) {
        val startMargin = if (toEnabled) THUMB_MARGIN_OFF_DP else THUMB_MARGIN_ON_DP
        val endMargin = if (toEnabled) THUMB_MARGIN_ON_DP else THUMB_MARGIN_OFF_DP
        val marginDelta = endMargin - startMargin

        for (step in 1..ANIMATION_STEPS) {
            val progress = step.toFloat() / ANIMATION_STEPS
            // Use ease-out interpolation for more natural feel
            val easedProgress = 1 - (1 - progress) * (1 - progress)
            val currentMargin = startMargin + (marginDelta * easedProgress)

            val views = RemoteViews(context.packageName, R.layout.widget_bike_mode)
            views.setViewLayoutMargin(
                R.id.widget_toggle_thumb,
                RemoteViews.MARGIN_START,
                currentMargin,
                TypedValue.COMPLEX_UNIT_DIP,
            )

            // Update track color at midpoint for smooth transition
            if (step == ANIMATION_STEPS / 2) {
                views.setInt(
                    R.id.widget_toggle_button,
                    "setBackgroundResource",
                    if (toEnabled) {
                        R.drawable.widget_toggle_track_on
                    } else {
                        R.drawable.widget_toggle_track_off
                    },
                )
            }

            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
            delay(STEP_DELAY_MS)
        }

        // Final update with exact end position and thumb drawable
        val finalViews = RemoteViews(context.packageName, R.layout.widget_bike_mode)
        finalViews.setViewLayoutMargin(
            R.id.widget_toggle_thumb,
            RemoteViews.MARGIN_START,
            endMargin,
            TypedValue.COMPLEX_UNIT_DIP,
        )
        finalViews.setInt(
            R.id.widget_toggle_thumb,
            "setBackgroundResource",
            if (toEnabled) {
                R.drawable.widget_toggle_thumb_on
            } else {
                R.drawable.widget_toggle_thumb_off
            },
        )
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, finalViews)
    }

    /**
     * Animates the widget background color transition.
     * Uses alpha blending simulation through intermediate drawable states.
     *
     * @param context Application context
     * @param appWidgetManager Widget manager instance
     * @param appWidgetId Widget ID to update
     * @param toEnabled Target state
     */
    suspend fun animateBackground(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        toEnabled: Boolean,
    ) {
        // Background transition happens in 3 steps with intermediate state
        val views = RemoteViews(context.packageName, R.layout.widget_bike_mode)

        // Step 1: Apply transition drawable
        views.setInt(
            R.id.widget_container,
            "setBackgroundResource",
            if (toEnabled) {
                R.drawable.widget_background_transition_to_active
            } else {
                R.drawable.widget_background_transition_to_inactive
            },
        )
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)

        // Wait for transition to complete
        delay(ANIMATION_DURATION_MS)

        // Step 2: Set final background
        val finalViews = RemoteViews(context.packageName, R.layout.widget_bike_mode)
        finalViews.setInt(
            R.id.widget_container,
            "setBackgroundResource",
            if (toEnabled) {
                R.drawable.widget_background_active
            } else {
                R.drawable.widget_background_inactive
            },
        )
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, finalViews)
    }

    /**
     * Animates the bike icon with a subtle pulse effect.
     *
     * @param context Application context
     * @param appWidgetManager Widget manager instance
     * @param appWidgetId Widget ID to update
     * @param toEnabled Target state
     */
    suspend fun animateIcon(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        toEnabled: Boolean,
    ) {
        val targetColor =
            context.getColor(
                if (toEnabled) R.color.status_active else R.color.status_inactive,
            )

        // Apply color change with slight delay for visual effect
        delay(ANIMATION_DURATION_MS / 3)

        val views = RemoteViews(context.packageName, R.layout.widget_bike_mode)
        views.setInt(R.id.widget_bike_icon, "setColorFilter", targetColor)
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
    }
}
