import SwiftUI
import UIKit
import Shared

/// Bridges the shared Compose Multiplatform UI into SwiftUI.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        // Compose handles insets itself (Scaffold), so let it draw edge to edge.
        ComposeView()
            .ignoresSafeArea()
    }
}
