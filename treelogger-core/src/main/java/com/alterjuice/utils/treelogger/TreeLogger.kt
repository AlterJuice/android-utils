package com.alterjuice.utils.treelogger




/**
 * A flexible and hierarchical logging utility for Kotlin.
 *
 * `TreeLogger` allows for organizing loggers in a tree-like structure where a logger's enabled state
 * can depend on its parent. It supports custom default tags, pluggable logging backends via the
 * [SimpleLogger] interface, and provides methods to transform and branch log output.
 *
 * Loggers are designed to be created and configured fluently, with transformation methods like
 * [withTag], [intercept], and [branch] returning new instances of `TreeLogger`, leaving the
 * original instance unchanged.
 *
 * This class implements the [Logger] interface, providing standard logging methods.
 *
 * @param tag The default tag for this logger instance. Used if no specific tag is provided at the log call site.
 * This parameter is part of the primary (protected) constructor.
 * @param sLogger The [SimpleLogger] backend responsible for the actual log writing.
 * This parameter is part of the primary (protected) constructor.
 * @param parentIsEnabled An optional lambda that returns the enabled state of a parent logger.
 * This enables hierarchical enabling/disabling of loggers.
 * This parameter is part of the primary (protected) constructor.
 */
open class TreeLogger protected constructor(
    private val tag: String? = null,
    private val sLogger: SimpleLogger,
    private val parentIsEnabled: (() -> Boolean)? = null,
) : Logger {

    /**
     * Creates a new `TreeLogger` instance with a default tag and a specific logger backend.
     *
     * @param tag Optional default tag for this logger instance. If null, messages might be logged
     * without a specific instance tag unless a tag is provided at the call site or by the [SimpleLogger] backend.
     * @param logger The [SimpleLogger] backend to use for writing log messages. Defaults to `SimpleLoggerImpl`
     * (a basic console-based implementation).
     */
    constructor(
        tag: String? = null,
        logger: SimpleLogger = SimpleLoggerImpl,
    ) : this(tag, logger, null)

    /**
     * Checks if this logger is currently enabled for logging.
     *
     * A logger is enabled if its own specific enabled state ([enable]/[disable] methods) is true
     * AND its parent (if one is defined via `parentIsEnabled`) is also enabled.
     * This allows for hierarchical enabling/disabling of log output.
     * By default, a logger is enabled unless its underlying [SimpleLogger] is [SimpleLogger.EMPTY].
     *
     * @return `true` if logging is enabled for this logger, `false` otherwise.
     */
    var isEnabled: Boolean = !isEmpty()
        /**
         * Gets the effective enabled state of this logger.
         * @return `true` if this logger's local state is enabled AND its parent (if any) is also enabled.
         */
        get() = field && (parentIsEnabled?.invoke() ?: true)
        private set


    /**
     * The core logging method. Logs a message if this logger is currently [isEnabled].
     *
     * If the `tag` parameter is null, the instance's default tag (`this.tag`) is used.
     * The actual log writing is delegated to the configured [sLogger] backend.
     *
     * @param level The [LogLevel] of the message.
     * @param tag Optional tag for this specific log message. If null, the instance's default tag is used.
     * @param msg The message string to log. Can be null.
     * @param thw Optional [Throwable] to log, including its stack trace.
     */
    override fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?) {
        if (!isEnabled) return
        sLogger.log(level, tag ?: this.tag, msg, thw)
    }

    /**
     * Logs a [Throwable] with the specified [LogLevel].
     *
     * The instance's default tag (`this.tag`) is used. The throwable's message ([Throwable.message])
     * is used as the log message.
     *
     * @param level The [LogLevel] for this log event.
     * @param thw The [Throwable] to log.
     */
    override fun log(level: LogLevel, thw: Throwable) {
        if (!isEnabled) return
        this.log(level = level, tag = this.tag, msg = thw.message, thw = thw)
    }

    /**
     * Logs a message composed of multiple arguments with the specified [LogLevel].
     *
     * Arguments are joined into a string, typically formatted as a list (e.g., `"[arg1, arg2]"`).
     * If the last argument in `args` is a [Throwable], it is treated as the throwable for this
     * log event, and the remaining arguments form the message. If `args` is empty and a throwable
     * is present, the throwable's message is used.
     * The instance's default tag (`this.tag`) is used.
     *
     * @param level The [LogLevel] for this log event.
     * @param args The arguments to log. The last argument can be a [Throwable].
     */
    override fun log(level: LogLevel, vararg args: Any?) {
        if (!isEnabled) return
        val throwable = args.lastOrNull() as? Throwable
        val messageArgs = if (throwable != null && args.isNotEmpty()) args.sliceArray(0 until args.size - 1) else args
        val msgString = if (messageArgs.isEmpty() && throwable != null) {
            throwable.message
        } else {
            messageArgs.joinToString(", ", "[", "]")
        }
        this.log(level = level, tag = this.tag, msg = msgString, thw = throwable)
    }

    /**
     * Logs a message string and a [Throwable] with the specified [LogLevel].
     *
     * @param level The [LogLevel] for this log event.
     * @param msg The message string.
     * @param thw The [Throwable] to log.
     */
    override fun log(level: LogLevel, msg: String, thw: Throwable) {
        if (!isEnabled) return
        this.log(level = level, tag = this.tag, msg = msg, thw = thw)
    }

    /**
     * Logs a message string with the specified [LogLevel] and an optional explicit tag.
     * If the `tag` parameter is null, the instance's default tag (`this.tag`) will be used by the main log method.
     *
     * @param level The [LogLevel] for this log event.
     * @param tag Optional tag for this specific message.
     * @param msg The message string.
     */
    override fun log(level: LogLevel, tag: String?, msg: String) {
        if (!isEnabled) return
        this.log(level = level, tag = tag, msg = msg, thw = null)
    }

    /**
     * Enables logging for this `TreeLogger` instance (sets its local enabled state to true).
     * Note: Logging will only occur if its parent (if any) is also enabled,
     * as checked by the [isEnabled] property's getter.
     */
    fun enable() {
        this.isEnabled = true
    }

    /**
     * Disables logging for this `TreeLogger` instance (sets its local enabled state to false).
     * This will also effectively disable logging for any child loggers that depend on this instance's
     * enabled state.
     */
    fun disable() {
        this.isEnabled = false
    }

    /**
     * Creates a new `TreeLogger` instance with the specified default tag.
     * The new logger inherits the [SimpleLogger] backend and parent's enabled state linkage
     * from this instance. This method returns a new instance, leaving the original unchanged.
     *
     * @param tag The new default tag for the returned logger.
     * @return A new `TreeLogger` instance with the updated default tag.
     */
    fun withTag(tag: String): TreeLogger = new(tag = tag)

    /**
     * Creates a new `TreeLogger` instance that intercepts and allows transformation of log event components
     * before they are passed to this logger's underlying [SimpleLogger] backend.
     *
     * The provided lambdas allow modification of the [LogLevel], message string, and [Throwable].
     * The `tag` of the log event (which is the resolved tag for the new logger when it's used)
     * is passed through to the original logger's backend without modification by this `intercept` method itself.
     * This method returns a new instance, leaving the original unchanged.
     *
     * @param iLevel Lambda to transform the [LogLevel]. Defaults to identity (no change).
     * @param iMsg Lambda to transform the message string. Defaults to identity.
     * @param iThw Lambda to transform the [Throwable]. Defaults to identity.
     * @return A new `TreeLogger` instance that applies the specified transformations to log events.
     */
    fun intercept(
        iLevel: (LogLevel) -> LogLevel = { it },
        iMsg: (String?) -> String? = { it },
        iThw: (Throwable?) -> Throwable? = { it },
        newTag: String? = this.tag,
    ): TreeLogger {
        if (isEmpty()) return this
        val newSimpleLogger = SimpleLogger { level, tag, msg, thw ->
            this.sLogger.log(level = iLevel(level), tag = tag, msg = iMsg(msg), thw = iThw(thw))
        }
        return new(simpleLogger = newSimpleLogger, tag = newTag)
    }

    /**
     * Convenience operator function, serving as an alias for [withTag].
     * Allows creating a new logger with a default tag using the syntax: `logger["MyNewTag"]`.
     *
     * @param tag The new default tag for the returned logger.
     * @return A new `TreeLogger` instance.
     * @see withTag
     */
    operator fun get(tag: String): TreeLogger = withTag(tag)

    /**
     * Convenience operator function to create a new logger with a default tag
     * derived from the calling context, typically the enclosing method name of the class of the
     * passed object `obj`. For example, `logger[this]` inside a method.
     *
     * Note: The reliability of inferring method names via `enclosingMethod` can be affected by
     * anonymous classes, lambdas, and code obfuscation (e.g., ProGuard/R8).
     *
     * @param obj An object (usually `this`) used to infer context for the tag.
     * @return A new `TreeLogger` instance with an inferred default tag.
     */
    operator fun get(obj: Any) = withTag(obj::class.java.enclosingMethod?.name ?: obj::class.simpleName ?: "Unknown")


    /**
     * Factory method to create a new `TreeLogger` instance, typically used for implementing
     * transformation methods like [withTag], [intercept], or [branch], and for extensibility by subclasses.
     *
     * This is an `open` function, allowing subclasses to override it to return instances
     * of their own type, thereby preserving the custom logger type throughout chained calls.
     *
     * The new logger is linked to this instance for hierarchical enabled state checking
     * by passing `this::isEnabled` as the `parentIsEnabled` lambda to the new logger's constructor.
     *
     * @param tag The default tag for the new logger. Defaults to the tag of this instance (`this.tag`).
     * @param simpleLogger The [SimpleLogger] backend for the new logger. Defaults to the `sLogger` of this instance.
     * @return A new `TreeLogger` instance.
     */
    open fun new(
        tag: String? = this.tag,
        simpleLogger: SimpleLogger = this.sLogger
    ): TreeLogger {
        return TreeLogger(tag, simpleLogger, ::isEnabled)
    }

    /**
     * Creates a new `TreeLogger` instance that will forward log events to both this logger's
     * original [SimpleLogger] backend AND the additionally provided `simpleLogger` parameter.
     *
     * This allows for "forking" or "branching" the log output to multiple destinations from a single
     * log call made on the returned (branched) logger. The new logger inherits its default tag
     * and parent's enabled state linkage from this instance.
     * This method returns a new instance, leaving the original unchanged.
     *
     * @param additionalSimpleLogger An additional [SimpleLogger] backend to also send log events to.
     * @return A new `TreeLogger` instance that logs to multiple backends.
     */
    open fun branch(additionalSimpleLogger: SimpleLogger): TreeLogger {
        return new(
            simpleLogger = { lvl, tag, msg, thw ->
                sLogger.log(lvl, tag, msg, thw) // Log to original consumer
                additionalSimpleLogger.log(lvl, tag, msg, thw) // Log to additional consumer
            }
        )
    }

    companion object {
        /**
         * A no-operation `TreeLogger` instance that produces no output.
         *
         * This logger is disabled by default (its [isEnabled] method will return `false` initially).
         * It utilizes [SimpleLogger.EMPTY] as its backend. It's useful for effectively silencing
         * logging in certain contexts or as a safe default placeholder.
         * Instantiated lazily.
         */
        val EMPTY by lazy { TreeLogger(tag = null, logger = SimpleLogger.EMPTY) }
    }
}