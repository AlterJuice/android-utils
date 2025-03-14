package com.alterjuice.utils.treelogger


fun interface SimpleLogger {
    fun log(level: LogLevel, msg: String)
    fun isEmpty() = this === EMPTY

    companion object {
        val EMPTY = SimpleLogger { level, msg -> }
    }
}
