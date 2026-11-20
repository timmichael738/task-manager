package com.kotlinapps.taskmanager.feature.edittask

import com.kotlinapps.taskmanager.core.model.Priority

data class AddEditTaskUiState(
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val showTitleError: Boolean = false,
    val taskNotFound: Boolean = false,
    val isSaved: Boolean = false,
)
