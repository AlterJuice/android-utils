package com.alterjuice.utils.treelogger


open class TreeLogger protected constructor(
    private val tag: String? = null,
    private val sLogger: SimpleLogger,
    private val parentIsEnabled: (() -> Boolean)? = null,
) : Logger {

    constructor(
        tag: String? = null,
        logger: SimpleLogger = SimpleLoggerImpl,
    ) : this(tag, logger, null)

    private var thisIsEnabled: Boolean = !this.isEmpty()

    fun isEnabled(): Boolean {
        return thisIsEnabled && (parentIsEnabled?.invoke()?: true)
    }

    override fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?) {
        if (!isEnabled()) return
        sLogger.log(level, tag?: this.tag, msg, thw)
    }

    override fun log(level: LogLevel, thw: Throwable) {
        if (!isEnabled()) return
        this.log(level = level, tag = this.tag, msg = thw.message, thw = thw)
    }

    override fun log(level: LogLevel, vararg args: Any) {
        if (!isEnabled()) return
        val throwable = args.lastOrNull() as? Throwable
        val messageArgs = if (throwable != null && args.isNotEmpty()) args.sliceArray(0 until args.size - 1) else args
        val msgString = if (messageArgs.isEmpty() && throwable != null) {
            throwable.message
        } else {
            messageArgs.joinToString(", ", "[", "]")
        }
        this.log(level = level, tag = this.tag, msg = msgString, thw = throwable)
    }

    override fun log(level: LogLevel, msg: String, thw: Throwable) {
        if (!isEnabled()) return
        log(level, tag = tag, msg = msg, thw = thw)
    }

    override fun log(level: LogLevel, tag: String?, msg: String) {
        if (!isEnabled()) return
        log(level, tag = tag, msg = msg, thw = null)
    }

    // State managing
    fun enable() {
        thisIsEnabled = true
    }

    fun disable() {
        thisIsEnabled = false
    }

    // Transformations operators
    fun withTag(tag: String): TreeLogger = new(tag = tag)

    fun intercept(
        iLevel: (LogLevel) -> LogLevel = { it },
        iMsg: (String?) -> String? = { it },
        iThw: (Throwable?) -> Throwable? = { it },
    ): TreeLogger {
        if (isEmpty()) return this
        return new(
            simpleLogger = { level, tag, msg, thw ->
                this.sLogger.log(
                    level = iLevel(level),
                    tag = tag,
                    msg = iMsg(msg),
                    thw = iThw(thw),
                )
            }
        )
    }

    operator fun get(tag: String): TreeLogger = withTag(tag)
    operator fun get(obj: Any) = withTag(obj::class.java.enclosingMethod?.name.toString())

    open fun new(
        tag: String? = this.tag,
        simpleLogger: SimpleLogger = sLogger,
    ): TreeLogger {
        return TreeLogger(tag, simpleLogger, ::isEnabled)
    }

    open fun branch(simpleLogger: SimpleLogger): TreeLogger {
        return new(
            simpleLogger = { lvl, tag, msg, thw ->
                sLogger.log(lvl, tag, msg, thw)
                simpleLogger.log(lvl, tag, msg, thw)
            }
        )
    }

    companion object {
        val EMPTY by lazy { TreeLogger(null, SimpleLogger.EMPTY) }
    }
}