package com.kotlinapps.taskmanager.feature.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kotlinapps.taskmanager.resources.*
import com.kotlinapps.taskmanager.core.designsystem.theme.TaskManagerTheme
import com.kotlinapps.taskmanager.core.model.Priority
import com.kotlinapps.taskmanager.core.model.Task
import com.kotlinapps.taskmanager.feature.tasks.components.TaskItem
import kotlin.time.Clock

/** Stateful entry point: wires the ViewModel to the stateless [TasksScreen]. */
@Composable
internal fun TasksRoute(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    viewModel: TasksViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val deletedMessage = stringResource(Res.string.task_deleted)
    val undoLabel = stringResource(Res.string.undo)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                TasksEvent.TaskDeleted -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(deletedMessage, actionLabel = undoLabel)
                    if (result == SnackbarResult.ActionPerformed) viewModel.onUndoDelete()
                }
            }
        }
    }

    TasksScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAddTask = onAddTask,
        onTaskClick = { onEditTask(it.id) },
        onCompletedChange = viewModel::onCompletedChange,
        onDeleteTask = viewModel::onDeleteTask,
        onFilterChange = viewModel::onFilterChange,
        onClearCompleted = viewModel::onClearCompleted,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TasksScreen(
    uiState: TasksUiState,
    snackbarHostState: SnackbarHostState,
    onAddTask: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onCompletedChange: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onFilterChange: (TasksFilter) -> Unit,
    onClearCompleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.tasks_title))
                        if (!uiState.isLoading) {
                            Text(
                                text = stringResource(Res.string.tasks_remaining, uiState.activeCount),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    if (uiState.hasCompletedTasks) {
                        IconButton(onClick = onClearCompleted) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_delete_sweep),
                                contentDescription = stringResource(Res.string.clear_completed),
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = stringResource(Res.string.add_task),
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            FilterRow(
                selected = uiState.filter,
                onFilterChange = onFilterChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                uiState.tasks.isEmpty() -> EmptyState(filter = uiState.filter)
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onCompletedChange = { onCompletedChange(task, it) },
                            onDelete = { onDeleteTask(task) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: TasksFilter,
    onFilterChange: (TasksFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = TasksFilter.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        filters.forEachIndexed { index, filter ->
            SegmentedButton(
                selected = filter == selected,
                onClick = { onFilterChange(filter) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = filters.size),
            ) {
                Text(
                    stringResource(
                        when (filter) {
                            TasksFilter.ALL -> Res.string.filter_all
                            TasksFilter.ACTIVE -> Res.string.filter_active
                            TasksFilter.COMPLETED -> Res.string.filter_completed
                        },
                    ),
                )
            }
        }
    }
}

@Composable
private fun EmptyState(filter: TasksFilter) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_task_alt),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(
                when (filter) {
                    TasksFilter.ALL -> Res.string.empty_all
                    TasksFilter.ACTIVE -> Res.string.empty_active
                    TasksFilter.COMPLETED -> Res.string.empty_completed
                },
            ),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TasksScreenPreview() {
    val now = Clock.System.now()
    TaskManagerTheme(dynamicColor = false) {
        TasksScreen(
            uiState = TasksUiState(
                isLoading = false,
                tasks = listOf(
                    Task(1, "Finish Kotlin roadmap", "Start with Task Manager", Priority.HIGH, false, now),
                    Task(2, "Buy groceries", priority = Priority.LOW, createdAt = now),
                    Task(3, "Read Compose docs", isCompleted = true, createdAt = now),
                ),
                activeCount = 2,
                hasCompletedTasks = true,
            ),
            snackbarHostState = SnackbarHostState(),
            onAddTask = {},
            onTaskClick = {},
            onCompletedChange = { _, _ -> },
            onDeleteTask = {},
            onFilterChange = {},
            onClearCompleted = {},
        )
    }
}
