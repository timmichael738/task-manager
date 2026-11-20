package com.kotlinapps.taskmanager.core.model

import kotlin.time.Instant

/**
 * Domain model for a task. This is what the UI and ViewModels work with;
 * it has no knowledge of how tasks are persisted.
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Instant,
)

enum class Priority { LOW, MEDIUM, HIGH }
