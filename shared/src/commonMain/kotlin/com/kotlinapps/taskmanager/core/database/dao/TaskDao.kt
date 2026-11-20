package com.kotlinapps.taskmanager.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kotlinapps.taskmanager.core.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /** Active tasks first, then by priority (high to low), then newest first. */
    @Query(
        """
        SELECT * FROM tasks
        ORDER BY
            is_completed ASC,
            CASE priority WHEN 'HIGH' THEN 0 WHEN 'MEDIUM' THEN 1 ELSE 2 END ASC,
            created_at DESC
        """,
    )
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    /** Inserts a new task or replaces an existing one with the same id. Returns the row id on insert. */
    @Upsert
    suspend fun upsert(task: TaskEntity): Long

    @Query("UPDATE tasks SET is_completed = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM tasks WHERE is_completed = 1")
    suspend fun deleteCompleted()
}
