# Task Manager

An offline task manager for **Android and iOS**, built with Kotlin Multiplatform and Compose Multiplatform.
One shared codebase provides the UI, ViewModels, database, and business logic for both platforms.

## Features

- Add, edit, and delete tasks (with **undo** from a snackbar)
- Low / Medium / High priority, shown as a color dot
- Filter by All / Active / Completed; "clear completed" in one tap
- Smart ordering: active first, then by priority, then newest
- Persists locally with Room; works fully offline
- Material 3, dark theme, edge-to-edge; Material You dynamic color on Android 12+

## Tech stack

| Concern | Library |
|---|---|
| UI | Compose Multiplatform, Material 3 |
| Architecture | MVVM, Unidirectional Data Flow, Repository pattern |
| DI | Koin |
| Persistence | Room (multiplatform) with bundled SQLite; schemas exported to `shared/schemas/` |
| Async | Kotlin Coroutines + Flow / StateFlow |
| Navigation | Navigation Compose (multiplatform) with type-safe `@Serializable` destinations |
| Resources | Compose Multiplatform resources (strings, vector icons) |
| Build | Gradle Kotlin DSL, Version Catalog (`gradle/libs.versions.toml`) |
| Testing | kotlin.test, kotlinx-coroutines-test, Turbine (run on Android JVM and iOS simulator) |

## Architecture

```
UI (Compose screen)  ──events──▶  ViewModel  ──calls──▶  TaskRepository  ──▶  TaskDao (Room)
        ▲                            │                         │
        └──────── StateFlow<UiState> ┘◀──── Flow<List<Task>> ──┘
```

- **Screens are stateless.** Each feature has a `*Route` composable (gets the ViewModel, collects state)
  and a `*Screen` composable (pure UI, previewable).
- **ViewModels expose a single immutable `UiState`** via `StateFlow`; one-off events (snackbars) use a `Channel`.
- **The repository is an interface**, so ViewModels are tested against an in-memory fake.
- **Platform code is minimal**: only the database file location and dynamic color differ per platform (`expect`/`actual`).

## Project structure

```
shared/                              # Kotlin Multiplatform module: everything shared
├── src/commonMain/kotlin/.../
│   ├── App.kt                       # Root composable used by both platforms
│   ├── di/                          # Koin modules + initKoin()
│   ├── navigation/                  # NavHost
│   ├── core/model | database | data | designsystem
│   └── feature/tasks | edittask     # Screen, ViewModel, UiState, navigation per feature
├── src/commonMain/composeResources/ # Strings and vector icons
├── src/androidMain/                 # Android DB path, dynamic color
├── src/iosMain/                     # iOS DB path, MainViewController
├── src/commonTest/                  # ViewModel tests (run on Android + iOS)
└── src/androidDeviceTest/           # Room DAO instrumented tests
androidApp/                          # Android launcher: Application + MainActivity
iosApp/                              # iOS launcher: SwiftUI app hosting the shared UI
```

## Build and run

**Requirements:** JDK 17, Android Studio (latest stable) with Android SDK Platform 37.1.
iOS additionally needs a Mac with Xcode 16+.

### Android

```bash
./gradlew :androidApp:installDebug          # install on a connected device/emulator
./gradlew :shared:testAndroidHostTest       # shared unit tests on the JVM
./gradlew :shared:connectedAndroidDeviceTest  # Room tests on a device/emulator
```

### iOS (macOS only)

```bash
./gradlew :shared:iosSimulatorArm64Test     # shared unit tests on the iOS simulator
open iosApp/iosApp.xcodeproj                # then press Run in Xcode
```

To run on a physical iPhone, set `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`.

CI builds and tests both platforms on every push (`.github/workflows/ci.yml`).

## Possible next steps

- Due dates and reminder notifications
- Swipe-to-delete and drag-to-reorder
- Search
- Compose UI tests for the screens
