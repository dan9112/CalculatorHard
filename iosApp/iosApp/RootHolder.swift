import Foundation
import Shared

public class RootHolder: ObservableObject {
    let lifecycle: LifecycleRegistry
    let rootComponent: RootComponent // ← Замени на точное имя из presentation

    init() {
        lifecycle = LifecycleRegistry()

        let context = DefaultComponentContext(
            lifecycle: lifecycle,
            stateKeeper: DefaultStateKeeper(),
            instanceKeeper: DefaultInstanceKeeper(),
            backHandler: DefaultBackHandler()
        )

        rootComponent = createRootComponent(context)

        LifecycleRegistryExtKt.create(lifecycle)
    }

    func resume()  { LifecycleRegistryExtKt.resume(lifecycle) }
    func pause()   { LifecycleRegistryExtKt.pause(lifecycle) }
    func destroy() { LifecycleRegistryExtKt.destroy(lifecycle) }
}
