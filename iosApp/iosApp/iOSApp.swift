import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init(){
        KoinDiKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
