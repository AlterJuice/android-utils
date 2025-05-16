package com.alterjuice.utils.treelogger

enum class LogLevel {
    VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT;
    val shortTag = name.first()
}