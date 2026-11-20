package com.kotlinapps.taskmanager.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.kotlinapps.taskmanager.core.database.dao.TaskDao
import com.kotlinapps.taskmanager.core.database.model.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.time.Instant

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
@ConstructedBy(TaskManagerDatabaseConstructor::class)
abstract class TaskManagerDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        const val FILE_NAME = "task-manager.db"
    }
}

/** Room generates the `actual` implementations for each platform. */
@Suppress("KotlinNoActualForExpect")
expect object TaskManagerDatabaseConstructor : RoomDatabaseConstructor<TaskManagerDatabase> {
    override fun initialize(): TaskManagerDatabase
}

/** Finishes a platform-specific builder with the shared configuration (bundled SQLite, IO dispatcher). */
fun RoomDatabase.Builder<TaskManagerDatabase>.buildDatabase(): TaskManagerDatabase =
    setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

internal class InstantConverter {
    @TypeConverter
    fun fromEpochMillis(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun toEpochMillis(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}
