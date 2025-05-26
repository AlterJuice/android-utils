package com.alterjuice.utils.treelogger

interface Logger : SimpleLogger {
    fun log(level: LogLevel, tag: String?, msg: String)
    fun log(level: LogLevel, vararg args: Any)
    fun log(level: LogLevel, msg: String, thw: Throwable)
    fun log(level: LogLevel, thw: Throwable)

    fun v(msg: String) = log(LogLevel.VERBOSE, tag = null, msg = msg)
    fun v(tag: String, msg: String) = log(LogLevel.VERBOSE, tag = tag, msg = msg)
    fun v(vararg args: Any) = log(LogLevel.VERBOSE, *args)
    fun v(msg: String, thw: Throwable) = log(LogLevel.VERBOSE, msg = msg, thw = thw)
    fun v(tag: String, msg: String, thw: Throwable) =
        log(LogLevel.VERBOSE, tag = tag, msg = msg, thw = thw)

    fun v(thw: Throwable) = log(LogLevel.VERBOSE, thw = thw)

    fun d(msg: String) = log(LogLevel.DEBUG, msg = msg, tag = null)
    fun d(tag: String, msg: String) = log(LogLevel.DEBUG, tag = tag, msg = msg)
    fun d(vararg args: Any) = log(LogLevel.DEBUG, *args)
    fun d(msg: String, thw: Throwable) = log(LogLevel.DEBUG, msg = msg, thw = thw)
    fun d(tag: String, msg: String, thw: Throwable) =
        log(LogLevel.DEBUG, tag = tag, msg = msg, thw = thw)

    fun d(thw: Throwable) = log(LogLevel.DEBUG, thw = thw)

    fun i(msg: String) = log(LogLevel.INFO, msg = msg, tag = null)
    fun i(tag: String, msg: String) = log(LogLevel.INFO, tag = tag, msg = msg)
    fun i(vararg args: Any) = log(LogLevel.INFO, *args)
    fun i(msg: String, thw: Throwable) = log(LogLevel.INFO, msg = msg, thw = thw)
    fun i(tag: String, msg: String, thw: Throwable) =
        log(LogLevel.INFO, tag = tag, msg = msg, thw = thw)

    fun i(thw: Throwable) = log(LogLevel.INFO, thw = thw)


    fun w(msg: String) = log(LogLevel.WARN, msg = msg, tag = null)
    fun w(tag: String, msg: String) = log(LogLevel.WARN, tag = tag, msg = msg)
    fun w(vararg args: Any) = log(LogLevel.WARN, *args)
    fun w(msg: String, thw: Throwable) = log(LogLevel.WARN, msg = msg, thw = thw)
    fun w(tag: String, msg: String, thw: Throwable) =
        log(LogLevel.WARN, tag = tag, msg = msg, thw = thw)

    fun w(thw: Throwable) = log(LogLevel.WARN, thw = thw)


    fun e(msg: String) = log(LogLevel.ERROR, msg = msg, tag = null)
    fun e(tag: String, msg: String) = log(LogLevel.ERROR, tag = tag, msg = msg)
    fun e(vararg args: Any) = log(LogLevel.ERROR, *args)
    fun e(msg: String, thw: Throwable) = log(LogLevel.ERROR, msg = msg, thw = thw)
    fun e(tag: String, msg: String, thw: Throwable) =
        log(LogLevel.ERROR, tag = tag, msg = msg, thw = thw)

    fun e(thw: Throwable) = log(LogLevel.ERROR, thw = thw)

    operator fun invoke(msg: String) = d(msg)
    operator fun invoke(tag: String, msg: String) = d(tag = tag, msg = msg)
    operator fun invoke(vararg args: Any) = d(*args)
    operator fun invoke(msg: String, thw: Throwable) = d(msg = msg, thw = thw)
    operator fun invoke(tag: String, msg: String, thw: Throwable) =
        d(tag = tag, msg = msg, thw = thw)

    operator fun invoke(thw: Throwable) = d(thw = thw)

    override fun isEmpty(): Boolean {
        return super.isEmpty() || this === EMPTY
    }

    companion object {
        val EMPTY by lazy { object : Logger {
            override fun log(level: LogLevel, tag: String?, msg: String) {}
            override fun log(level: LogLevel, vararg args: Any) {}
            override fun log(level: LogLevel, msg: String, thw: Throwable) {}
            override fun log(level: LogLevel, thw: Throwable) {}
            override fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?) {}
        } }
    }
}