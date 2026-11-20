package com.kotlinapps.taskmanager.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.kotlinapps.taskmanager.core.database.TaskManagerDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<RoomDatabase.Builder<TaskManagerDatabase>> {
        val context = androidContext()
        Room.databaseBuilder<TaskManagerDatabase>(
            context = context,
            name = context.getDatabasePath(TaskManagerDatabase.FILE_NAME).absolutePath,
        )
    }
}
