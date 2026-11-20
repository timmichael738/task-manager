package com.kotlinapps.taskmanager

import android.app.Application
import com.kotlinapps.taskmanager.di.initKoin
import org.koin.android.ext.koin.androidContext

class TaskManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TaskManagerApplication)
        }
    }
}
