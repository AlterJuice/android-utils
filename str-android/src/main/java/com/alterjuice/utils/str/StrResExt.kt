package com.alterjuice.utils.str

import android.content.Context

fun StrRes.get(context: Context): String {
    return when(this) {
        is StrRes.Plural -> context.resources.getQuantityString(id, quantity, *this.unpackArgs(context))
        is StrRes.Text -> context.getString(id, *this.unpackArgs(context))
        is StrRes.Transformable -> str.get(context).applyTransforms(transforms)
    }
}


fun Str.get(context: Context): String = when (this) {
    is StrRes -> this.get(context)
    is StrRaw -> this.get()
    else -> throw IllegalStateException("Unsupported")
}


fun Str.getOrEmpty(context: Context): String = when (this) {
    is StrRes -> this.get(context)
    is StrRaw -> this.get()
    else -> ""
}

fun StrRes.ArgsHolder.unpackArgs(context: Context) = this.args.map {
    if (it is Str) it.get(context) else it
}.toTypedArray()

