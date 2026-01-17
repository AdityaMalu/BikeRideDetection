package com.example.bikeridedetection.ui.util

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

/**
 * Modifier extension for marking a composable as a heading for screen readers.
 */
fun Modifier.accessibilityHeading(): Modifier = this.semantics { heading() }

/**
 * Modifier extension for providing a custom content description.
 */
fun Modifier.accessibilityDescription(description: String): Modifier =
    this.semantics { contentDescription = description }

/**
 * Modifier extension for providing a state description (e.g., "On" or "Off").
 */
fun Modifier.accessibilityState(state: String): Modifier = this.semantics { stateDescription = state }

/**
 * Modifier extension for marking a composable as a button for screen readers.
 */
fun Modifier.accessibilityButton(description: String): Modifier =
    this.semantics {
        role = Role.Button
        contentDescription = description
    }

/**
 * Modifier extension for marking a composable as a switch for screen readers.
 */
fun Modifier.accessibilitySwitch(
    description: String,
    isChecked: Boolean,
): Modifier =
    this.semantics {
        role = Role.Switch
        contentDescription = description
        stateDescription = if (isChecked) "On" else "Off"
    }

/**
 * Modifier extension for grouping related content for screen readers.
 * This merges all child semantics into a single announcement.
 */
fun Modifier.accessibilityGroup(description: String): Modifier =
    this.clearAndSetSemantics { contentDescription = description }

/**
 * Composable that requests focus when first composed.
 * Useful for directing screen reader focus to important content.
 */
@Composable
fun rememberFocusRequester(): FocusRequester = remember { FocusRequester() }

/**
 * Modifier extension for requesting focus on composition.
 */
@Composable
fun Modifier.requestFocusOnMount(): Modifier {
    val focusRequester = rememberFocusRequester()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    return this.focusRequester(focusRequester).focusable()
}

private const val MINUTES_PER_HOUR = 60

/**
 * Formats a duration in a screen reader friendly way.
 */
fun formatDurationForAccessibility(minutes: Int): String =
    when {
        minutes < MINUTES_PER_HOUR -> "$minutes minutes"
        minutes == MINUTES_PER_HOUR -> "1 hour"
        minutes % MINUTES_PER_HOUR == 0 -> "${minutes / MINUTES_PER_HOUR} hours"
        else -> "${minutes / MINUTES_PER_HOUR} hours and ${minutes % MINUTES_PER_HOUR} minutes"
    }

/**
 * Formats a percentage in a screen reader friendly way.
 */
fun formatPercentageForAccessibility(percentage: Int): String = "$percentage percent"
