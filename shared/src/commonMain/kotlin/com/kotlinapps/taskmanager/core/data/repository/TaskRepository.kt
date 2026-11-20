package com.kotlinapps.taskmanager.core.data.repository

import com.kotlinapps.taskmanager.core.model.Task
import kotlinx.coroutines.flow.Flow

/** Single source of truth for tasks. ViewModels depend on this interface, never on Room directly. */
interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>

    suspend fun getTask(id: Long): Task?

    /** Creates the task when [Task.id] is 0, otherwise updates it. Returns the task id. */
    suspend fun saveTask(task: Task): Long

    suspend fun setCompleted(id: Long, completed: Boolean)

    suspend fun deleteTask(id: Long)

    suspend fun clearCompleted()
}
