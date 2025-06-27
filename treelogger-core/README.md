# TreeLogger 🌳
`TreeLogger` is a flexible, hierarchical, and platform-independent logging system for Kotlin, designed to simplify common tasks and provide full control over the logging process.

> 💡 **For a deep dive into the architecture, design principles, and more examples, check out my detailed article on Medium!**
> 
> **➡️ [Write Cleaner, Smarter Logs: Introducing TreeLogger for Kotlin](https://medium.com/@bohdan.snurnitsyn/write-cleaner-smarter-logs-introducing-treelogger-for-kotlin-794a531ffa30)**


## 🌳 Key Concepts
Standard tools like println or android.util.Log are simple but limited. Popular libraries like Timber significantly improve the experience but are often tied to the Android framework, making them unsuitable for use in pure Kotlin/KMP modules.

TreeLogger was created to solve two main problems:

- Platform-independence: The library's core has no Android dependencies, allowing it to be used in any Kotlin project.
- True hierarchical control: The ability to enable or disable logging for entire modules or functional branches with a single call.

## 🚀 Installation
The library is modular. The core functionality is in treelogger-core, and Android-specific utilities are in treelogger-android.

> 💡 Note: Version 1.0.15 is used as an example. Please check the repository's Releases page for the latest available version.

### Step 1: Add the JitPack repository
First, add the JitPack repository to your project's settings.gradle.kts file.

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // <-- Add this line
    }
}
```

### Step 2: Add the library to your version catalog (libs.versions.toml)
```toml
[versions]
treelogger = "1.0.15"

[libraries]
alterjuice-treelogger-core = { module = "com.github.AlterJuice.android-utils:treelogger-core", version.ref = "treelogger" }
alterjuice-treelogger-android = { module = "com.github.AlterJuice.android-utils:treelogger-android", version.ref = "treelogger" }
```

### Step 3: Add the dependency to your module's build.gradle.kts
For pure Kotlin or KMP modules:
```gradle
dependencies {
    implementation(libs.alterjuice.treelogger.core)
}

```
For Android modules:
```gradle
dependencies {
    implementation(libs.alterjuice.treelogger.android)
}
```

## 🏛️ Architecture: Separation of Concerns
The key idea behind TreeLogger is a clear separation of duties between two components:

TreeLogger (the brain): Responsible for managing logs. It knows about hierarchy, tags, levels, and applies transformations to events. This is the API you work with.

SimpleLogger (the hands): Responsible for the final action with the log. It's a simple functional interface that receives the prepared data and performs one action—writes it.

```kotlin
fun interface SimpleLogger {
    fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?)
}
```

This architecture makes the system incredibly flexible. You can plug in any "hands" (SimpleLogger) to perform the tasks you need.

## ✨ SimpleLogger Examples
Here are a few implementation examples that demonstrate the flexibility of this approach.

### 1. Basic Console Output
Ideal for server-side applications or KMP modules.

```kotlin
object ConsoleSimpleLogger : SimpleLogger {
    override fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?) {
        println("${level.name} [${tag ?: "NoTag"}]: $msg")
        thw?.printStackTrace()
    }
}
```

### 2. Structured JSON Logging
For analytics or sending logs to a server.

```kotlin
class JsonSimpleLogger(
    private val actualOutput: (Map<String, Any?>) -> Unit,
    private val extrasBuilder: () -> Map<String, Any?> = { emptyMap() }
) : SimpleLogger {
    override fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?) {
        val map = buildMap {
            putAll(extrasBuilder()) // Add custom fields (userId, sessionId, etc.)
            put("level", level.name)
            tag?.let { put("tag", it) }
            msg?.let { put("message", it) }
            // ... logic for thw
        }
        actualOutput(map)
    }
}
```

### 3. Decorator for Encryption
For writing encrypted logs to a file, using the "Decorator" pattern.

```kotlin
class CryptoSimpleLogger(
    private val encryptor: YourEncryptor,
    private val downstreamLogger: SimpleLogger
) : SimpleLogger {
    override fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?) {
        val encryptedMsg = msg?.let { encryptor.encrypt(it) }
        downstreamLogger.log(level, tag, encryptedMsg, thw)
    }
}
```

## 🎬 Key Features in Action
### 1. Hierarchical Control — The Superpower
The most powerful feature. A logger's state depends on its parents.

```kotlin
val baseLogger = TreeLogger(tag = "MyApp")
val networkLogger = baseLogger.withTag("Network")
val uiLogger = baseLogger.withTag("UI")

networkLogger.disable() // Disable logs from the network layer
uiLogger.d("Button was clicked") // Will be logged
networkLogger.d("Request sent")    // Will NOT be logged
```

### 2. Log Branching (branch)
Send a single log to multiple destinations at once.

```kotlin
val crashlyticsBackend = SimpleLogger { _, _, _, thw ->
    thw?.let { FirebaseCrashlytics.getInstance().recordException(it) }
}

// Writes to both Logcat and Crashlytics
val criticalErrorLogger = AndroidTreeLogger.branch(crashlyticsBackend)

criticalErrorLogger.e("Critical error during payment!", thw = e)
```

### 3. Event Transformation (intercept)
Modify log events on the fly. This method allows you to change the level, message, and even set a new default tag for the new instance.

```kotlin
// A more complex chain
val securePaymentsLogger = baseLogger
    .intercept(
      newTag = "Payments", 
      iMsg = { msg -> "[SECURE] $msg" } // Obscure sensitive data
    )
    .branch(FileSimpleLogger("payments.log")) // And also write to a file
```

## 🔌 Extensibility
While most daily needs are covered by custom SimpleLogger implementations, TreeLogger itself is also an open class with an open factory method new(). This is intended for advanced scenarios where you might need to inherit from TreeLogger to create a specialized logger type (e.g., a PrefixedLogger that automatically adds a prefix to every message) and preserve its type in chained calls to withTag or intercept. This proves that the architecture was designed with future expansion in mind.

## 🏁 Future Plans
Add more out-of-the-box SimpleLogger implementations (for files with rotation, for popular services).
[] Write comprehensive tests.
[] Improve the documentation.

### I hope you find this tool useful. TreeLogger is an open-source project. Your feedback, GitHub stars, and contributions are very welcome!
