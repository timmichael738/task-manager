package com.kotlinapps.taskmanager.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kotlinapps.taskmanager.core.database.dao.TaskDao
import com.kotlinapps.taskmanager.core.database.model.TaskEntity
import com.kotlinapps.taskmanager.core.model.Priority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Instant

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: TaskManagerDatabase
    private lateinit var dao: TaskDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder<TaskManagerDatabase>(
            InstrumentationRegistry.getInstrumentation().targetContext,
        ).setDriver(BundledSQLiteDriver()).build()
        dao = database.taskDao()
    }

    @After
    fun closeDb() = database.close()

    private fun entity(title: String, priority: Priority, completed: Boolean = false) = TaskEntity(
        title = title,
        description = "",
        priority = priority,
        isCompleted = completed,
        createdAt = Instant.fromEpochMilliseconds(0),
    )

    @Test
    fun observeAll_ordersActiveFirstThenByPriority() = runTest {
        dao.upsert(entity("low", Priority.LOW))
        dao.upsert(entity("done-high", Priority.HIGH, completed = true))
        dao.upsert(entity("high", Priority.HIGH))
        dao.upsert(entity("medium", Priority.MEDIUM))

        val titles = dao.observeAll().first().map { it.title }
        assertEquals(listOf("high", "medium", "low", "done-high"), titles)
    }

    @Test
    fun setCompleted_andDeleteCompleted() = runTest {
        val id = dao.upsert(entity("task", Priority.MEDIUM))
        dao.setCompleted(id, true)
        assertTrue(dao.getById(id)!!.isCompleted)

        dao.deleteCompleted()
        assertNull(dao.getById(id))
    }
}
