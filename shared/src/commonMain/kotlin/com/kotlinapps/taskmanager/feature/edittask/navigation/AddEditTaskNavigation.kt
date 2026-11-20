package com.kotlinapps.taskmanager.feature.edittask.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kotlinapps.taskmanager.feature.edittask.AddEditTaskRoute
import kotlinx.serialization.Serializable

@Serializable
data object AddTaskDestination

@Serializable
data class EditTaskDestination(val taskId: Long)

fun NavController.navigateToAddTask() = navigate(AddTaskDestination)

fun NavController.navigateToEditTask(taskId: Long) = navigate(EditTaskDestination(taskId))

fun NavGraphBuilder.addEditTaskScreens(onNavigateUp: () -> Unit) {
    composable<AddTaskDestination> {
        AddEditTaskRoute(taskId = null, onNavigateUp = onNavigateUp)
    }
    composable<EditTaskDestination> { backStackEntry ->
        val destination = backStackEntry.toRoute<EditTaskDestination>()
        AddEditTaskRoute(taskId = destination.taskId, onNavigateUp = onNavigateUp)
    }
}
