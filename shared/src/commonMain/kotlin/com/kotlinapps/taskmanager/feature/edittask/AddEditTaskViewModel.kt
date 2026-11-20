package com.kotlinapps.taskmanager.feature.edittask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinapps.taskmanager.core.data.repository.TaskRepository
import com.kotlinapps.taskmanager.core.model.Priority
import com.kotlinapps.taskmanager.core.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * Handles both creating ([taskId] == null) and editing a task.
 * The id is passed as a Koin parameter, which keeps the ViewModel free of navigation types and easy to test.
 */
class AddEditTaskViewModel(
    private val taskId: Long?,
    private val taskRepository: TaskRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTaskUiState(isEditing = taskId != null))
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    /** The persisted task being edited, so fields not shown in the form are preserved on save. */
    private var existingTask: Task? = null

    init {
        if (taskId != null) loadTask(taskId)
    }

    private fun loadTask(id: Long) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val task = taskRepository.getTask(id)
            existingTask = task
            _uiState.update { state ->
                if (task == null) {
                    state.copy(isLoading = false, taskNotFound = true)
                } else {
                    state.copy(
                        isLoading = false,
                        title = task.title,
                        description = task.description,
                        priority = task.priority,
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, showTitleError = false) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.isLoading || state.taskNotFound) return
        if (state.title.isBlank()) {
            _uiState.update { it.copy(showTitleError = true) }
            return
        }

        val task = existingTask?.copy(
            title = state.title.trim(),
            description = state.description.trim(),
            priority = state.priority,
        ) ?: Task(
            title = state.title.trim(),
            description = state.description.trim(),
            priority = state.priority,
            createdAt = clock.now(),
        )

        viewModelScope.launch {
            taskRepository.saveTask(task)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
