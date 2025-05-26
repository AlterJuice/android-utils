package com.alterjuice.utils.treelogger

import android.util.Log

object AndroidTreeLogger : TreeLogger(null, sLogger = SimpleLogger { level, tag, msg, thw ->
    when (level) {
        LogLevel.VERBOSE -> Log.v(tag, msg, thw)
        LogLevel.DEBUG -> Log.d(tag, msg, thw)
        LogLevel.INFO -> Log.i(tag, msg, thw)
        LogLevel.WARN -> Log.w(tag, msg, thw)
        LogLevel.ERROR -> Log.e(tag, msg, thw)
        LogLevel.ASSERT -> Log.wtf(tag, msg, thw)
    }
})