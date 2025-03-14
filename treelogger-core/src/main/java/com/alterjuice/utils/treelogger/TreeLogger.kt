package com.alterjuice.utils.treelogger


object SimpleTreeLogger: TreeLogger(SimpleLogger { level, msg -> println("[${level}]: $msg") })


open class TreeLogger(
    private val sLogger: SimpleLogger = SimpleLogger { level, msg ->
        println("[${level}]: $msg")
    },
) : Logger {
    private var enabled: Boolean = true

    override fun log(level: LogLevel, msg: String) {
        if (!enabled) return
        sLogger.log(level, msg)
    }

    override fun log(level: LogLevel, thw: Throwable) =
        log(level, thw.stackTraceToString())

    override fun log(level: LogLevel, vararg args: Any) =
        log(level, msg = args.joinToString(", ", "[", "]"))

    override fun log(level: LogLevel, msg: String, thw: Throwable) =
        log(level, msg = msgWithExceptionToString(msg, thw))

    override fun log(level: LogLevel, tag: String, msg: String) =
        log(level, msg = msgWithTag(tag, msg))

    override fun log(level: LogLevel, tag: String, msg: String, thw: Throwable) =
        log(level, msg = msgWithTag(tag, msgWithExceptionToString(msg, thw)))

    // State managing
    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    // Transformations operators
    fun withTag(tag: String): TreeLogger {
        return transform(transformText = { level, msg -> msgWithTag(tag, msg) })
    }
    fun transformText(
        transformText: (LogLevel, String) -> String
    ): TreeLogger {
        if (isEmpty()) return this
        return new { level, msg ->
            this.log(level = level, msg = transformText(level, msg))
        }
    }

    fun transform(
        transformLevel: (LogLevel) -> LogLevel = { it },
        transformText: (LogLevel, String) -> String = { level, msg -> msg },
    ): TreeLogger {
        if (isEmpty()) return this
        return new { level, msg ->
            this.log(level = transformLevel(level), msg = transformText(level, msg))
        }
    }

    operator fun get(tag: String): TreeLogger = withTag(tag)
    operator fun get(obj: Any) = withTag(obj::class.java.enclosingMethod?.name.toString())

    open fun new(singleLogger: SimpleLogger): TreeLogger {
        return TreeLogger(singleLogger)
    }

    protected fun msgWithExceptionToString(msg: String, thw: Throwable): String {
        return "$msg\n${thw.stackTraceToString()}"
    }

    protected fun msgWithTag(tag: String, msg: String): String {
        return "[${tag}]$msg"
    }
    companion object {
        val EMPTY get() = TreeLogger(SimpleLogger.EMPTY).isEmpty()
    }
}