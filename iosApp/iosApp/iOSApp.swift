import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Start dependency injection once, before any shared UI is created.
        MainViewControllerKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
