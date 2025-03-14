package com.alterjuice.utils.str

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

fun Res.get(context: Context): String {
    return when(this) {
        is Res.Plural -> context.resources.getQuantityString(id, quantity, *this.unpackArgs(context))
        is Res.String -> context.getString(id, *this.unpackArgs(context))
        is Res.Transformable -> str.get(context).applyTransforms(transforms)
    }
}


fun Str.get(context: Context): String = when (this) {
    is Res -> this.get(context)
    is Raw -> this.get()
    else -> throw IllegalStateException("Unsupported")
}


fun Str.getOrEmpty(context: Context): String = when (this) {
    is Res -> this.get(context)
    is Raw -> this.get()
    else -> ""
}

fun Res.ArgsHolder.unpackArgs(context: Context) = this.args.map {
    if (it is Str) it.get(context) else it
}.toTypedArray()


operator fun Str.Companion.invoke(@StringRes id: Int, vararg args: Any?) = Res.String(id, *args)
operator fun Str.Companion.invoke(@PluralsRes id: Int, quantity: Int, vararg args: Any?) = Res.Plural(id, quantity, *args)
