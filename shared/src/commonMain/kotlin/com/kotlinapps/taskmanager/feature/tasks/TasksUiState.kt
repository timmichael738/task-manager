package com.kotlinapps.taskmanager.feature.tasks

import com.kotlinapps.taskmanager.core.model.Task

enum class TasksFilter { ALL, ACTIVE, COMPLETED }

data class TasksUiState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val filter: TasksFilter = TasksFilter.ALL,
    val activeCount: Int = 0,
    val hasCompletedTasks: Boolean = false,
)

/** One-off events the UI should react to exactly once (e.g. showing a snackbar). */
sealed interface TasksEvent {
    data object TaskDeleted : TasksEvent
}
