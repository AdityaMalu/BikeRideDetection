package com.example.bikeridedetection.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Safe Cyclist Color Palette - Light Mode
private val PrimaryLight = Color(0xFF2E7D32)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFA5D6A7)
private val OnPrimaryContainerLight = Color(0xFF002204)

private val SecondaryLight = Color(0xFFFF6F00)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFFFE0B2)
private val OnSecondaryContainerLight = Color(0xFF331A00)

private val TertiaryLight = Color(0xFF1565C0)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFBBDEFB)
private val OnTertiaryContainerLight = Color(0xFF001D36)

private val ErrorLight = Color(0xFFD32F2F)
private val OnErrorLight = Color(0xFFFFFFFF)

private val SurfaceLight = Color(0xFFFEFEFE)
private val OnSurfaceLight = Color(0xFF1C1B1F)
private val SurfaceVariantLight = Color(0xFFE7E0EC)
private val OnSurfaceVariantLight = Color(0xFF49454F)

private val BackgroundLight = Color(0xFFFFFBFE)
private val OnBackgroundLight = Color(0xFF1C1B1F)

// Safe Cyclist Color Palette - Dark Mode
private val PrimaryDark = Color(0xFF81C784)
private val OnPrimaryDark = Color(0xFF003910)
private val PrimaryContainerDark = Color(0xFF1B5E20)
private val OnPrimaryContainerDark = Color(0xFFA5D6A7)

private val SecondaryDark = Color(0xFFFFB74D)
private val OnSecondaryDark = Color(0xFF4A2800)
private val SecondaryContainerDark = Color(0xFFE65100)
private val OnSecondaryContainerDark = Color(0xFFFFE0B2)

private val TertiaryDark = Color(0xFF64B5F6)
private val OnTertiaryDark = Color(0xFF003258)
private val TertiaryContainerDark = Color(0xFF0D47A1)
private val OnTertiaryContainerDark = Color(0xFFBBDEFB)

private val ErrorDark = Color(0xFFEF5350)
private val OnErrorDark = Color(0xFF690005)

private val SurfaceDark = Color(0xFF1C1B1F)
private val OnSurfaceDark = Color(0xFFE6E1E5)
private val SurfaceVariantDark = Color(0xFF49454F)
private val OnSurfaceVariantDark = Color(0xFFCAC4D0)

private val BackgroundDark = Color(0xFF1C1B1F)
private val OnBackgroundDark = Color(0xFFE6E1E5)

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryLight,
        onPrimary = OnPrimaryLight,
        primaryContainer = PrimaryContainerLight,
        onPrimaryContainer = OnPrimaryContainerLight,
        secondary = SecondaryLight,
        onSecondary = OnSecondaryLight,
        secondaryContainer = SecondaryContainerLight,
        onSecondaryContainer = OnSecondaryContainerLight,
        tertiary = TertiaryLight,
        onTertiary = OnTertiaryLight,
        tertiaryContainer = TertiaryContainerLight,
        onTertiaryContainer = OnTertiaryContainerLight,
        error = ErrorLight,
        onError = OnErrorLight,
        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = OnSurfaceVariantLight,
        background = BackgroundLight,
        onBackground = OnBackgroundLight,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryDark,
        onPrimary = OnPrimaryDark,
        primaryContainer = PrimaryContainerDark,
        onPrimaryContainer = OnPrimaryContainerDark,
        secondary = SecondaryDark,
        onSecondary = OnSecondaryDark,
        secondaryContainer = SecondaryContainerDark,
        onSecondaryContainer = OnSecondaryContainerDark,
        tertiary = TertiaryDark,
        onTertiary = OnTertiaryDark,
        tertiaryContainer = TertiaryContainerDark,
        onTertiaryContainer = OnTertiaryContainerDark,
        error = ErrorDark,
        onError = OnErrorDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
    )

@Composable
fun BikeRideDetectionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
