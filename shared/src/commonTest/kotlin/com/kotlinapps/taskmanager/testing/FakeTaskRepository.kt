package com.kotlinapps.taskmanager.testing

import com.kotlinapps.taskmanager.core.data.repository.TaskRepository
import com.kotlinapps.taskmanager.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** In-memory [TaskRepository] for fast, deterministic ViewModel tests. */
class FakeTaskRepository : TaskRepository {

    private val tasks = MutableStateFlow<Map<Long, Task>>(emptyMap())
    private var nextId = 1L

    val currentTasks: List<Task> get() = tasks.value.values.toList()

    fun setTasks(vararg newTasks: Task) {
        tasks.value = newTasks.associateBy { it.id }
        nextId = (newTasks.maxOfOrNull { it.id } ?: 0L) + 1
    }

    override fun observeTasks(): Flow<List<Task>> = tasks.map { it.values.toList() }

    override suspend fun getTask(id: Long): Task? = tasks.value[id]

    override suspend fun saveTask(task: Task): Long {
        val id = if (task.id == 0L) nextId++ else task.id
        tasks.update { it + (id to task.copy(id = id)) }
        return id
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        tasks.update { map -> map[id]?.let { map + (id to it.copy(isCompleted = completed)) } ?: map }
    }

    override suspend fun deleteTask(id: Long) {
        tasks.update { it - id }
    }

    override suspend fun clearCompleted() {
        tasks.update { map -> map.filterValues { !it.isCompleted } }
    }
}
