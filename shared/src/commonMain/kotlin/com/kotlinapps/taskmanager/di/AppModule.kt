package com.kotlinapps.taskmanager.di

import androidx.room.RoomDatabase
import com.kotlinapps.taskmanager.core.data.repository.OfflineTaskRepository
import com.kotlinapps.taskmanager.core.data.repository.TaskRepository
import com.kotlinapps.taskmanager.core.database.TaskManagerDatabase
import com.kotlinapps.taskmanager.core.database.buildDatabase
import com.kotlinapps.taskmanager.feature.edittask.AddEditTaskViewModel
import com.kotlinapps.taskmanager.feature.tasks.TasksViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.time.Clock

/** Provides the platform-specific `RoomDatabase.Builder<TaskManagerDatabase>`. */
internal expect val platformModule: Module

internal val dataModule = module {
    single<TaskManagerDatabase> { get<RoomDatabase.Builder<TaskManagerDatabase>>().buildDatabase() }
    single { get<TaskManagerDatabase>().taskDao() }
    single<TaskRepository> { OfflineTaskRepository(get()) }
    // Injected so time-dependent logic can be tested with a fixed clock.
    single<Clock> { Clock.System }
}

internal val viewModelModule = module {
    viewModelOf(::TasksViewModel)
    viewModel { params -> AddEditTaskViewModel(taskId = params.getOrNull(), taskRepository = get(), clock = get()) }
}

/** Single entry point used by both the Android Application and the iOS app. */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, dataModule, viewModelModule)
    }
}
