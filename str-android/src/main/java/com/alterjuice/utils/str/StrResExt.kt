package com.alterjuice.utils.str

import android.content.Context

fun Str.getOrElse(context: Context, default: () -> String): String = when (this) {
    is StrRes -> this.get(context)
    is StrRaw -> this.get()
    else -> default()
}

fun Str.get(context: Context): String = getOrElse(context) {
    throw IllegalStateException("Unsupported")
}

fun Str.getOrEmpty(context: Context) = getOrElse(context) { "" }

fun StrResArgsHolder.unpackArgs(context: Context) = this.args.map {
    if (it is Str) it.get(context) else it
}.toTypedArray()
