package com.kotlinapps.taskmanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** iOS has no wallpaper-based color system, so the brand palette is always used. */
@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null
