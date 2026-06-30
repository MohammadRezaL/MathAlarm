package com.example.mathalarm.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AlarmPrimaryLight,
    onPrimary = AlarmOnPrimaryLight,
    primaryContainer = AlarmPrimaryContainerLight,
    onPrimaryContainer = AlarmOnPrimaryContainerLight,
    background = AlarmBackgroundLight,
    onBackground = AlarmOnBackgroundLight,
    surface = AlarmSurfaceLight,
    onSurface = AlarmOnSurfaceLight,
    error = AlarmErrorLight,
    onError = AlarmOnErrorLight
)

private val DarkColorScheme = darkColorScheme(
    primary = AlarmPrimaryDark,
    onPrimary = AlarmOnPrimaryDark,
    primaryContainer = AlarmPrimaryContainerDark,
    onPrimaryContainer = AlarmOnPrimaryContainerDark,
    background = AlarmBackgroundDark,
    onBackground = AlarmOnBackgroundDark,
    surface = AlarmSurfaceDark,
    onSurface = AlarmOnSurfaceDark,
    error = AlarmErrorDark,
    onError = AlarmOnErrorDark
)

@Composable
fun MathAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MathAlarmTypography,
        content = content
    )
}