package com.example.bikeridedetection.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.bikeridedetection.R
import com.example.bikeridedetection.ui.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * AppWidgetProvider for the Bike Mode toggle widget.
 * Displays current bike mode status and allows users to toggle it directly from the home screen.
 */
class BikeModeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        Timber.d("Widget onUpdate called for ${appWidgetIds.size} widgets")
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TOGGLE_BIKE_MODE -> {
                Timber.d("Widget toggle action received")
                // Start the service to toggle bike mode
                val serviceIntent = Intent(context, BikeModeWidgetService::class.java).apply {
                    action = BikeModeWidgetService.ACTION_TOGGLE
                }
                context.startService(serviceIntent)
            }
            ACTION_UPDATE_WIDGET -> {
                val isEnabled = intent.getBooleanExtra(EXTRA_IS_ENABLED, false)
                val animate = intent.getBooleanExtra(EXTRA_ANIMATE, true)
                Timber.d("Widget update action received, isEnabled: $isEnabled, animate: $animate")
                if (animate) {
                    updateAllWidgetsAnimated(context, isEnabled)
                } else {
                    updateAllWidgets(context, isEnabled)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Timber.d("Widget enabled - first widget added")
        // Start observing bike mode state
        val serviceIntent = Intent(context, BikeModeWidgetService::class.java).apply {
            action = BikeModeWidgetService.ACTION_START_OBSERVING
        }
        context.startService(serviceIntent)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Timber.d("Widget disabled - last widget removed")
        // Stop observing bike mode state
        val serviceIntent = Intent(context, BikeModeWidgetService::class.java).apply {
            action = BikeModeWidgetService.ACTION_STOP_OBSERVING
        }
        context.startService(serviceIntent)
    }

    companion object {
        const val ACTION_TOGGLE_BIKE_MODE = "com.example.bikeridedetection.TOGGLE_BIKE_MODE"
        const val ACTION_UPDATE_WIDGET = "com.example.bikeridedetection.UPDATE_WIDGET"
        const val EXTRA_IS_ENABLED = "extra_is_enabled"
        const val EXTRA_ANIMATE = "extra_animate"

        // Coroutine scope for animations
        private val animationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        /**
         * Updates all widget instances with the current bike mode state (no animation).
         */
        fun updateAllWidgets(context: Context, isEnabled: Boolean) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, BikeModeWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            Timber.d("Updating ${appWidgetIds.size} widgets with isEnabled: $isEnabled")
            for (appWidgetId in appWidgetIds) {
                updateWidget(context, appWidgetManager, appWidgetId, isEnabled)
            }
        }

        /**
         * Updates all widget instances with smooth animations.
         */
        fun updateAllWidgetsAnimated(context: Context, isEnabled: Boolean) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, BikeModeWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            Timber.d("Animating ${appWidgetIds.size} widgets to isEnabled: $isEnabled")
            for (appWidgetId in appWidgetIds) {
                animationScope.launch {
                    updateWidgetAnimated(context, appWidgetManager, appWidgetId, isEnabled)
                }
            }
        }

        /**
         * Updates a single widget with smooth animations.
         */
        private suspend fun updateWidgetAnimated(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            isEnabled: Boolean,
        ) {
            // Launch all animations concurrently for synchronized effect
            kotlinx.coroutines.coroutineScope {
                // Animate toggle thumb sliding
                launch {
                    WidgetAnimationHelper.animateToggleThumb(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        isEnabled,
                    )
                }

                // Animate background color transition
                launch {
                    WidgetAnimationHelper.animateBackground(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        isEnabled,
                    )
                }

                // Animate icon color change
                launch {
                    WidgetAnimationHelper.animateIcon(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        isEnabled,
                    )
                }

                // Update text with fade effect (using partial updates)
                launch {
                    animateTextChange(context, appWidgetManager, appWidgetId, isEnabled)
                }
            }

            // Final complete update to ensure all states are correct
            updateWidget(context, appWidgetManager, appWidgetId, isEnabled)
        }

        /**
         * Animates text changes with a fade effect simulation.
         */
        private suspend fun animateTextChange(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            isEnabled: Boolean,
        ) {
            // Brief delay before text change for visual effect
            kotlinx.coroutines.delay(100)

            val views = RemoteViews(context.packageName, R.layout.widget_bike_mode)

            // Update status text
            views.setTextViewText(
                R.id.widget_status_text,
                context.getString(
                    if (isEnabled) R.string.widget_status_active
                    else R.string.widget_status_ready,
                ),
            )

            // Update status description
            views.setTextViewText(
                R.id.widget_status_description,
                context.getString(
                    if (isEnabled) R.string.widget_calls_blocked
                    else R.string.widget_tap_to_enable,
                ),
            )

            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }

        /**
         * Updates a single widget instance.
         */
        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            isEnabled: Boolean,
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_bike_mode)

            // Update background based on state - green when active
            views.setInt(
                R.id.widget_container,
                "setBackgroundResource",
                if (isEnabled) R.drawable.widget_background_active
                else R.drawable.widget_background_inactive,
            )

            // Update bike icon tint
            views.setInt(
                R.id.widget_bike_icon,
                "setColorFilter",
                context.getColor(if (isEnabled) R.color.status_active else R.color.status_inactive),
            )

            // Update status text
            views.setTextViewText(
                R.id.widget_status_text,
                context.getString(
                    if (isEnabled) R.string.widget_status_active
                    else R.string.widget_status_ready,
                ),
            )

            // Update status description
            views.setTextViewText(
                R.id.widget_status_description,
                context.getString(
                    if (isEnabled) R.string.widget_calls_blocked
                    else R.string.widget_tap_to_enable,
                ),
            )

            // Update toggle switch track
            views.setInt(
                R.id.widget_toggle_button,
                "setBackgroundResource",
                if (isEnabled) R.drawable.widget_toggle_track_on
                else R.drawable.widget_toggle_track_off,
            )

            // Update toggle switch thumb drawable
            views.setInt(
                R.id.widget_toggle_thumb,
                "setBackgroundResource",
                if (isEnabled) R.drawable.widget_toggle_thumb_on
                else R.drawable.widget_toggle_thumb_off,
            )

            // Update toggle thumb position using layout params
            // For ON state: thumb moves to the right (end)
            // For OFF state: thumb stays at the left (start)
            val thumbMarginStart = if (isEnabled) {
                context.resources.getDimensionPixelSize(R.dimen.widget_toggle_thumb_margin_on)
            } else {
                context.resources.getDimensionPixelSize(R.dimen.widget_toggle_thumb_margin_off)
            }
            views.setViewLayoutMargin(
                R.id.widget_toggle_thumb,
                RemoteViews.MARGIN_START,
                thumbMarginStart.toFloat(),
                android.util.TypedValue.COMPLEX_UNIT_PX,
            )

            // Set click intent for toggle switch
            val toggleIntent = Intent(context, BikeModeWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_BIKE_MODE
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.widget_toggle_button, togglePendingIntent)

            // Set click intent for widget container to open app
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                1,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

