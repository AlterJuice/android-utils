package com.alterjuice.utils.treelogger

/**
 * A [SimpleLogger] implementation that structures log events as a `Map<String, Any?>`
 * and then passes this map to a provided [actualOutput] lambda for further processing.
 *
 * This logger is designed for structured logging, where the log data is first collected
 * into a map. The consumer lambda ([actualOutput]) can then decide how to handle this map,
 * for example, by serializing it to a JSON string, printing it, sending it over a network,
 * or storing it in a database.
 *
 * It also supports adding custom, potentially dynamic, fields to each log entry via the
 * optional [extrasBuilder] lambda.
 *
 * @param actualOutput A lambda function that consumes the generated `Map<String, Any?>` for each log event.
 * This lambda is responsible for any subsequent processing or output of the structured log data.
 * @param extrasBuilder An optional lambda function that, when provided, is invoked for each log event
 * to return a `Map<String, Any?>` of additional custom fields. These fields
 * are merged into the main log data map. Defaults to `null`, meaning no extra
 * fields are added by default.
 */

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