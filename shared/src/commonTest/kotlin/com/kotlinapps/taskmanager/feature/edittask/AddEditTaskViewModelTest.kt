package com.kotlinapps.taskmanager.feature.edittask

import com.kotlinapps.taskmanager.core.model.Priority
import com.kotlinapps.taskmanager.core.model.Task
import com.kotlinapps.taskmanager.testing.FakeTaskRepository
import com.kotlinapps.taskmanager.testing.MainDispatcherTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

class AddEditTaskViewModelTest : MainDispatcherTest() {

    private val now = Instant.parse("2026-01-01T10:00:00Z")
    private val clock = object : Clock {
        override fun now(): Instant = this@AddEditTaskViewModelTest.now
    }
    private val repository = FakeTaskRepository()

    private fun createViewModel(taskId: Long? = null) = AddEditTaskViewModel(taskId, repository, clock)

    @Test
    fun saveWithBlankTitle_showsErrorAndDoesNotSave() {
        val viewModel = createViewModel()
        viewModel.onTitleChange("   ")
        viewModel.onSave()

        assertTrue(viewModel.uiState.value.showTitleError)
        assertFalse(viewModel.uiState.value.isSaved)
        assertTrue(repository.currentTasks.isEmpty())
    }

    @Test
    fun typingTitle_clearsError() {
        val viewModel = createViewModel()
        viewModel.onSave()
        viewModel.onTitleChange("A")
        assertFalse(viewModel.uiState.value.showTitleError)
    }

    @Test
    fun saveNewTask_persistsTrimmedValuesWithClockTime() {
        val viewModel = createViewModel()
        viewModel.onTitleChange("  Buy milk  ")
        viewModel.onDescriptionChange(" 2 liters ")
        viewModel.onPriorityChange(Priority.HIGH)
        viewModel.onSave()

        val saved = repository.currentTasks.single()
        assertEquals("Buy milk", saved.title)
        assertEquals("2 liters", saved.description)
        assertEquals(Priority.HIGH, saved.priority)
        assertEquals(now, saved.createdAt)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun editExistingTask_loadsFieldsAndPreservesCompletionAndCreatedAt() {
        val created = Instant.parse("2025-06-01T00:00:00Z")
        repository.setTasks(Task(id = 7, title = "Old", isCompleted = true, createdAt = created))
        val viewModel = createViewModel(taskId = 7)

        assertTrue(viewModel.uiState.value.isEditing)
        assertEquals("Old", viewModel.uiState.value.title)

        viewModel.onTitleChange("New")
        viewModel.onSave()

        val saved = repository.currentTasks.single()
        assertEquals(7L, saved.id)
        assertEquals("New", saved.title)
        assertTrue(saved.isCompleted)
        assertEquals(created, saved.createdAt)
    }

    @Test
    fun editMissingTask_showsNotFound() {
        val viewModel = createViewModel(taskId = 99)
        assertTrue(viewModel.uiState.value.taskNotFound)

        viewModel.onSave()
        assertFalse(viewModel.uiState.value.isSaved)
    }
}
