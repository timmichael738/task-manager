package com.kotlinapps.taskmanager.feature.edittask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kotlinapps.taskmanager.resources.*
import com.kotlinapps.taskmanager.core.designsystem.theme.TaskManagerTheme
import com.kotlinapps.taskmanager.core.model.Priority

@Composable
internal fun AddEditTaskRoute(
    taskId: Long?,
    onNavigateUp: () -> Unit,
    viewModel: AddEditTaskViewModel = koinViewModel(
        key = taskId?.toString() ?: "new",
        parameters = { parametersOf(taskId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateUp()
    }

    AddEditTaskScreen(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onSave = viewModel::onSave,
        onNavigateUp = onNavigateUp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditTaskScreen(
    uiState: AddEditTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onSave: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (uiState.isEditing) Res.string.edit_task else Res.string.new_task)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(Res.string.navigate_up),
                        )
                    }
                },
                actions = {
                    if (!uiState.isLoading && !uiState.taskNotFound) {
                        IconButton(onClick = onSave) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_check),
                                contentDescription = stringResource(Res.string.save),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.taskNotFound -> Text(
                    text = stringResource(Res.string.error_task_not_found),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                else -> TaskForm(
                    uiState = uiState,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onPriorityChange = onPriorityChange,
                )
            }
        }
    }
}

@Composable
private fun TaskForm(
    uiState: AddEditTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(Res.string.title_label)) },
            isError = uiState.showTitleError,
            supportingText = if (uiState.showTitleError) {
                { Text(stringResource(Res.string.error_title_required)) }
            } else {
                null
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(Res.string.description_label)) },
            minLines = 3,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(stringResource(Res.string.priority_label), style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Priority.entries.forEach { priority ->
                FilterChip(
                    selected = uiState.priority == priority,
                    onClick = { onPriorityChange(priority) },
                    label = {
                        Text(
                            stringResource(
                                when (priority) {
                                    Priority.LOW -> Res.string.priority_low
                                    Priority.MEDIUM -> Res.string.priority_medium
                                    Priority.HIGH -> Res.string.priority_high
                                },
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditTaskScreenPreview() {
    TaskManagerTheme(dynamicColor = false) {
        AddEditTaskScreen(
            uiState = AddEditTaskUiState(title = "Write README", priority = Priority.HIGH),
            onTitleChange = {},
            onDescriptionChange = {},
            onPriorityChange = {},
            onSave = {},
            onNavigateUp = {},
        )
    }
}
