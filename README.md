This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


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(this)

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(geeta)

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…