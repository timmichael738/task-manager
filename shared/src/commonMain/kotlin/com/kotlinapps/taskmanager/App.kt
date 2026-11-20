package com.kotlinapps.taskmanager

import androidx.compose.runtime.Composable
import com.kotlinapps.taskmanager.core.designsystem.theme.TaskManagerTheme
import com.kotlinapps.taskmanager.navigation.TaskManagerNavHost

/** Root composable shared by Android and iOS. */
@Composable
fun App() {
    TaskManagerTheme {
        TaskManagerNavHost()
    }
}
