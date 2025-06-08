package com.alterjuice.utils.treelogger



internal object SimpleLoggerImpl: SimpleLogger {
    override fun log(
        level: LogLevel,
        tag: String?,
        msg: String?,
        thw: Throwable?,
    ) {
        println(buildString {
            append("[${level.shortTag}]")
            tag?.let { append("[${it}]")}
            append(msg)
            thw?.let { append(it.stackTraceToString()) }
        })
    }
}
class SimpleTreeLogger(tag: String? = null): TreeLogger(tag, SimpleLoggerImpl)
