package com.kotlinapps.taskmanager.feature.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinapps.taskmanager.core.data.repository.TaskRepository
import com.kotlinapps.taskmanager.core.model.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val filter = MutableStateFlow(TasksFilter.ALL)

    private val _events = Channel<TasksEvent>(Channel.BUFFERED)
    val events: Flow<TasksEvent> = _events.receiveAsFlow()

    /** Kept so a deletion can be undone from the snackbar. */
    private var recentlyDeletedTask: Task? = null

    val uiState: StateFlow<TasksUiState> =
        combine(taskRepository.observeTasks(), filter) { tasks, filter ->
            TasksUiState(
                isLoading = false,
                tasks = when (filter) {
                    TasksFilter.ALL -> tasks
                    TasksFilter.ACTIVE -> tasks.filterNot { it.isCompleted }
                    TasksFilter.COMPLETED -> tasks.filter { it.isCompleted }
                },
                filter = filter,
                activeCount = tasks.count { !it.isCompleted },
                hasCompletedTasks = tasks.any { it.isCompleted },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TasksUiState(),
        )

    fun onFilterChange(newFilter: TasksFilter) {
        filter.value = newFilter
    }

    fun onCompletedChange(task: Task, completed: Boolean) {
        viewModelScope.launch { taskRepository.setCompleted(task.id, completed) }
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task.id)
            recentlyDeletedTask = task
            _events.send(TasksEvent.TaskDeleted)
        }
    }

    fun onUndoDelete() {
        val task = recentlyDeletedTask ?: return
        recentlyDeletedTask = null
        viewModelScope.launch { taskRepository.saveTask(task) }
    }

    fun onClearCompleted() {
        viewModelScope.launch { taskRepository.clearCompleted() }
    }
}
