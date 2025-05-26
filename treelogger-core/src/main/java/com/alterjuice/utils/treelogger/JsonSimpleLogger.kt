package com.alterjuice.utils.treelogger

class JsonSimpleLogger(
    private val actualOutput: (Map<String, Any?>) -> Unit,
    private val extrasBuilder: (() -> Map<String, Any?>)? = null
) : SimpleLogger {
    override fun log(level: LogLevel, tag: String?, msg: String?, thw: Throwable?) {
        val map = buildMap {
            extrasBuilder?.let { putAll(it.invoke())}
            put("level", level.name)
            tag?.let { put("tag", it) }
            msg?.let { put("msg", it) }
            thw?.let {
                put("thw", mapOf(
                    "type" to thw::class.simpleName,
                    "msg" to thw.message,
                    "stacktrace" to thw.stackTraceToString()
                ))
            }
        }
        actualOutput(map)
    }
}



private fun main() {
    val logger = JsonSimpleLogger(
        actualOutput = { map -> println(map) },
        extrasBuilder = {
            mapOf(
                "timestamp" to System.currentTimeMillis().toString(),
                "thread" to Thread.currentThread().name
            )
        }
    )
    logger.log(LogLevel.INFO, "tag", "msg", RuntimeException("thw"))
    logger.log(LogLevel.INFO, "tag1", "Message", null)
}