package com.kotlinapps.taskmanager.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Indigo40,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo20,
    secondary = Teal40,
    error = Red40,
)

private val DarkColors = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    secondary = Teal80,
    error = Red80,
)

/**
 * Wallpaper-based Material You colors. Returns null where unsupported
 * (iOS, and Android 11 or lower), in which case the brand palette is used.
 */
@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?

@Composable
fun TaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = (if (dynamicColor) dynamicColorScheme(darkTheme) else null)
        ?: if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
