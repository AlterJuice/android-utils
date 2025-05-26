package com.alterjuice.utils.treelogger


fun interface SimpleLogger {
    fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?)
    fun isEmpty() = this === EMPTY

    companion object {
        val EMPTY by lazy { SimpleLogger { level, tag, msg, thw -> } }
    }
}
