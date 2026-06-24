This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform
  applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
      folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part,
      the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose
  Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for
  your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run
widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run
widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run
widget
in your IDE's toolbar or run it directly from the terminal:

- for the Wasm target (faster, modern browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
      ```
- for the JS target (slower, supports older browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:jsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
      ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run
widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack
channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them
on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).


Plugins at work
.ignore (4.5.7)
AndroidXmlSorter (1.1.1)
Better Kotlin-Java Completion (1.1.1)
CI Aid for GitLab (2.0.0)
CodeGlance Pro (2.0.6)
Compose Color Preview (0.0.10-261)
D Language (1.39.4)
Dart (506.1.0)
Database Navigator (3.7.4.0)
Docker (261.23567.72)
Doki Theme v2 (88.5-1.16.4)
Flutter (93.0.0)
GigaCode (26.5.16-242)
GitHub Workflow (2026.6.20)
Gradle Version Catalogs (1.4.0)
Gradle Wrapper Mirror (1.0.2)
Hyperskill Academy (2026.13-2026.1)
JetBrains Marketplace Licensing (261.23567.212)
KDoc-er - Kotlin Doc Generator (2026.1.1)
kdoc-formatter-ide-plugin (1.7.0)
KMP Project View (1.1.0)
Koin Dependency Injection (Official) (1.5.3)
kotest (6.1.18-2026.1.1)
Kotlin Code Sorter (1.2.2)
Kotlin Multiplatform (261.23567.78-AS)
LSP4IJ (0.20.1)
Noctumsempra's Color MEGAPACK (1.0.3.7)
Protocol Buffers (261.22158.185)
Space (261.23567.28)
SQLDelight (2.3.2)
Translation (3.8.3)
Valkyrie - SVG to ImageVector (1.6.0)
Android (261.23567.138.2611.15646644)
Android Design Tools (261.23567.138.2611.15646644)
Jetpack Compose (261.23567.138.2611.15646644)
Smali Support (261.23567.138.2611.15646644)
Artifacts Repository Search (261.23567.138.2611.15646644)
Gradle (261.23567.138.2611.15646644)
Gradle for Java (261.23567.138.2611.15646644)
HTML Tools (261.23567.138.2611.15646644)
Configuration Script (261.23567.138.2611.15646644)
Copyright (261.23567.138.2611.15646644)
EditorConfig (261.23567.138.2611.15646644)
Java IDE Customization (261.23567.138.2611.15646644)
Code Coverage for Java (261.23567.138.2611.15646644)
Java Bytecode Decompiler (261.23567.138.2611.15646644)
Java Internationalization (261.23567.138.2611.15646644)
Java Stream Debugger (261.23567.138.2611.15646644)
Eclipse Keymap (261.23567.138.2611.15646644)
NetBeans Keymap (261.23567.138.2611.15646644)
Visual Studio Keymap (261.23567.138.2611.15646644)
Compose Multiplatform (261.23567.138.2611.15646644)
C/C++ Language Support via Classic Engine (261.23567.138.2611.15646644)
Groovy (261.23567.138.2611.15646644)
Java (261.23567.138.2611.15646644)
JSON (261.23567.138.2611.15646644)
Kotlin (261.23567.138.2611.15646644-AS)
Markdown (261.23567.138.2611.15646644)
Natural Languages (261.23567.138.2611.15646644)
Properties (261.23567.138.2611.15646644)
Shell Script (261.23567.138.2611.15646644)
TextMate Bundles (261.23567.138.2611.15646644)
Toml (261.23567.138.2611.15646644)
YAML (261.23567.138.2611.15646644)
Machine Learning Code Completion (261.23567.138.2611.15646644)
DevKit Runtime (261.23567.138.2611.15646644)
Performance Testing (261.23567.138.2611.15646644)
JUnit (261.23567.138.2611.15646644)
TestNG (261.23567.138.2611.15646644)
Git (261.23567.138.2611.15646644)
GitHub (261.23567.138.2611.15646644)
GitLab (261.23567.138.2611.15646644)
Mercurial (261.23567.138.2611.15646644)
Modal Commit Interface (261.23567.138.2611.15646644)
Android APK Support (261.23567.138.2611.15646644)
Android NDK Support (261.23567.138.2611.15646644)
Android SDK Upgrade Assistant (261.23567.138.2611.15646644)
App Links Assistant (261.23567.138.2611.15646644)
Clangd Support (261.23567.138.2611.15646644)
Device Streaming (261.23567.138.2611.15646644)
Firebase Services (261.23567.138.2611.15646644)
Gemini (261.23567.138.2611.15646644)
Git for App Insights (261.23567.138.2611.15646644)
GMD Code Completion (261.23567.138.2611.15646644)
Gradle Declarative Support (261.23567.138.2611.15646644)
Gradle DSL Parser Support (261.23567.138.2611.15646644)
Groovy Live Templates (261.23567.138.2611.15646644)
Images (261.23567.138.2611.15646644)
Task Management (261.23567.138.2611.15646644)
Terminal (261.23567.138.2611.15646644)
Test Recorder (261.23567.138.2611.15646644)
WebP Support (261.23567.138.2611.15646644)
