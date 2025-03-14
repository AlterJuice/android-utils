package com.alterjuice.utils.treelogger

import android.util.Log

object AndroidTreeLogger : TreeLogger(SimpleLogger { level, msg ->
    when (level) {
        LogLevel.VERBOSE -> Log.v(null, msg)
        LogLevel.DEBUG -> Log.d(null, msg)
        LogLevel.INFO -> Log.i(null, msg)
        LogLevel.WARN -> Log.w(null, msg)
        LogLevel.ERROR -> Log.e(null, msg)
        LogLevel.ASSERT -> Log.wtf(null, msg)
    }
})