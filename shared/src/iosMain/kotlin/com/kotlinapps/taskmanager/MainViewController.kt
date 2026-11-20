package com.kotlinapps.taskmanager

import androidx.compose.ui.window.ComposeUIViewController
import com.kotlinapps.taskmanager.di.initKoin
import platform.UIKit.UIViewController

/** Called once from Swift (`iOSApp.init`) before any UI is shown. */
fun startKoinIos() = initKoin()

/** Hosts the shared Compose UI inside the iOS app. */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
