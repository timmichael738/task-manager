package com.kotlinapps.taskmanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kotlinapps.taskmanager.feature.edittask.navigation.addEditTaskScreens
import com.kotlinapps.taskmanager.feature.edittask.navigation.navigateToAddTask
import com.kotlinapps.taskmanager.feature.edittask.navigation.navigateToEditTask
import com.kotlinapps.taskmanager.feature.tasks.navigation.TasksDestination
import com.kotlinapps.taskmanager.feature.tasks.navigation.tasksScreen

/** App-level navigation graph. Each feature contributes its own destinations. */
@Composable
fun TaskManagerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = TasksDestination,
        modifier = modifier,
    ) {
        tasksScreen(
            onAddTask = navController::navigateToAddTask,
            onEditTask = navController::navigateToEditTask,
        )
        addEditTaskScreens(
            onNavigateUp = navController::navigateUp,
        )
    }
}
