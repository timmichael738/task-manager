package com.kotlinapps.taskmanager.feature.tasks.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kotlinapps.taskmanager.feature.tasks.TasksRoute
import kotlinx.serialization.Serializable

@Serializable
data object TasksDestination

fun NavGraphBuilder.tasksScreen(
    onAddTask: () -> Unit,
    onEditTask: (taskId: Long) -> Unit,
) {
    composable<TasksDestination> {
        TasksRoute(onAddTask = onAddTask, onEditTask = onEditTask)
    }
}
