package com.kotlinapps.taskmanager.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kotlinapps.taskmanager.core.model.Priority
import com.kotlinapps.taskmanager.core.model.Task
import kotlin.time.Instant

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
)

fun TaskEntity.asExternalModel() = Task(
    id = id,
    title = title,
    description = description,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt,
)

fun Task.asEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt,
)
