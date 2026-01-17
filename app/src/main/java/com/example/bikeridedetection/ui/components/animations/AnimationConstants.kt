package com.example.bikeridedetection.ui.components.animations

/**
 * Animation duration and easing constants for consistent animations across the app.
 * Following Material Design 3 motion guidelines.
 */
object AnimationConstants {
    // Duration constants (in milliseconds)
    const val DURATION_SHORT = 150
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 500
    const val DURATION_EXTRA_LONG = 700

    // Shimmer animation
    const val SHIMMER_DURATION = 1200
    const val SHIMMER_DELAY = 200

    // Pulse animation
    const val PULSE_DURATION = 1000
    const val PULSE_MIN_SCALE = 0.95f
    const val PULSE_MAX_SCALE = 1.05f

    // Fade animation
    const val FADE_IN_DURATION = DURATION_MEDIUM
    const val FADE_OUT_DURATION = DURATION_SHORT

    // Scale animation
    const val SCALE_PRESSED = 0.96f
    const val SCALE_NORMAL = 1f

    // Rotation animation
    const val ROTATION_FULL = 360f

    // Loading indicator
    const val LOADING_ROTATION_DURATION = 1000
    const val LOADING_STROKE_WIDTH = 4f
}
