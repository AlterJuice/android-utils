package com.alterjuice.utils.str

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes


/**
 * @see Str
 * @see StrRaw
 * */
sealed interface StrRes : Str {

    data class Plural(@PluralsRes val id: Int, val quantity: Int, override val args: List<Any?>) : StrRes, StrResArgsHolder {
        constructor(id: Int, quantity: Int, vararg args: Any?): this(id, quantity, args.toList())
    }

    data class Text(@StringRes val id: Int, override val args: List<Any?>) : StrRes, StrResArgsHolder {
        constructor(id: Int, vararg args: Any?): this(id, args.toList())
    }

    class Transformable internal constructor(
        internal val str: StrRes,
        internal val transforms: List<(String) -> String>
    ) : StrRes

    fun get(context: Context): String = when(this) {
        is Plural -> context.resources.getQuantityString(id, quantity, *this.unpackArgs(context))
        is Text -> context.getString(id, *this.unpackArgs(context))
        is Transformable -> str.get(context).applyTransforms(transforms)
    }

    override fun transform(transformBlock: (String) -> String): Str = when (this) {
        is Transformable -> Transformable(this.str, this.transforms + transformBlock)
        else -> Transformable(this, listOf(transformBlock))
    }

    companion object {
        operator fun invoke(@StringRes id: Int, vararg args: Any?) = Text(id, *args)
        operator fun invoke(@PluralsRes id: Int, quantity: Int, vararg args: Any?) = Plural(id, quantity, *args)
    }
}


interface StrResArgsHolder {
    val args: List<Any?>

}