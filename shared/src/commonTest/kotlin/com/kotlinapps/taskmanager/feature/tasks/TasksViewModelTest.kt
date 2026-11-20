package com.kotlinapps.taskmanager.feature.tasks

import app.cash.turbine.test
import com.kotlinapps.taskmanager.core.model.Task
import com.kotlinapps.taskmanager.testing.FakeTaskRepository
import com.kotlinapps.taskmanager.testing.MainDispatcherTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class TasksViewModelTest : MainDispatcherTest() {

    private val repository = FakeTaskRepository()
    private lateinit var viewModel: TasksViewModel

    private val now = Instant.parse("2026-01-01T10:00:00Z")
    private val active = Task(id = 1, title = "Active", createdAt = now)
    private val done = Task(id = 2, title = "Done", isCompleted = true, createdAt = now)

    @BeforeTest
    fun setUp() {
        repository.setTasks(active, done)
        viewModel = TasksViewModel(repository)
    }

    /** uiState uses WhileSubscribed, so tests must keep a subscriber active. */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.subscribe() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
    }

    @Test
    fun initialState_isLoading() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun uiState_containsAllTasksAndCounts() = runTest {
        subscribe()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.tasks.size)
        assertEquals(1, state.activeCount)
        assertTrue(state.hasCompletedTasks)
    }

    @Test
    fun filterActive_showsOnlyIncompleteTasks() = runTest {
        subscribe()
        viewModel.onFilterChange(TasksFilter.ACTIVE)
        assertEquals(listOf(active), viewModel.uiState.value.tasks)
    }

    @Test
    fun filterCompleted_showsOnlyCompletedTasks() = runTest {
        subscribe()
        viewModel.onFilterChange(TasksFilter.COMPLETED)
        assertEquals(listOf(done), viewModel.uiState.value.tasks)
    }

    @Test
    fun completingTask_updatesActiveCount() = runTest {
        subscribe()
        viewModel.onCompletedChange(active, true)
        assertEquals(0, viewModel.uiState.value.activeCount)
    }

    @Test
    fun deleteTask_emitsEventAndUndoRestoresIt() = runTest {
        subscribe()
        viewModel.events.test {
            viewModel.onDeleteTask(active)
            assertEquals(TasksEvent.TaskDeleted, awaitItem())
        }
        assertFalse(viewModel.uiState.value.tasks.contains(active))

        viewModel.onUndoDelete()
        assertTrue(viewModel.uiState.value.tasks.contains(active))
    }

    @Test
    fun clearCompleted_removesOnlyCompletedTasks() = runTest {
        subscribe()
        viewModel.onClearCompleted()
        assertEquals(listOf(active), viewModel.uiState.value.tasks)
        assertFalse(viewModel.uiState.value.hasCompletedTasks)
    }
}
