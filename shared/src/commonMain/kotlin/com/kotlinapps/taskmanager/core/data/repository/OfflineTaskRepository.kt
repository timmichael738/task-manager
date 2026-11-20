package com.kotlinapps.taskmanager.core.data.repository

import com.kotlinapps.taskmanager.core.database.dao.TaskDao
import com.kotlinapps.taskmanager.core.database.model.asEntity
import com.kotlinapps.taskmanager.core.database.model.asExternalModel
import com.kotlinapps.taskmanager.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** [TaskRepository] backed by the local Room database. Room's suspend/Flow APIs are main-safe. */
internal class OfflineTaskRepository(
    private val taskDao: TaskDao,
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> =
        taskDao.observeAll().map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun getTask(id: Long): Task? = taskDao.getById(id)?.asExternalModel()

    override suspend fun saveTask(task: Task): Long {
        val rowId = taskDao.upsert(task.asEntity())
        // Upsert returns -1 when it updated an existing row.
        return if (task.id == 0L) rowId else task.id
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) = taskDao.setCompleted(id, completed)

    override suspend fun deleteTask(id: Long) = taskDao.deleteById(id)

    override suspend fun clearCompleted() = taskDao.deleteCompleted()
}
