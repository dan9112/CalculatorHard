import SwiftUI
import Shared

@main
struct iOSApp: App { // ← Оставь своё оригинальное имя структуры
    @StateObject private var rootHolder = RootHolder()
    @Environment(\.scenePhase) private var scenePhase

    var body: some Scene {
        WindowGroup {
            ComposeRootView(rootHolder: rootHolder)
                .ignoresSafeArea()
        }
        .onChange(of: scenePhase) { newPhase in
            switch newPhase {
            case .active:
                rootHolder.resume()
            case .inactive, .background:
                rootHolder.pause()
            @unknown default:
                break
            }
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.willTerminateNotification)) { _ in
            rootHolder.destroy()
        }
    }
}

struct ComposeRootView: UIViewControllerRepresentable {
    let rootHolder: RootHolder

    func makeUIViewController(context: Context) -> UIViewController {
        // Топ-левел функция в Kotlin экспортируется с суффиксом Kt
        return MainViewControllerKt.MainViewController(rootHolder.rootComponent)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
