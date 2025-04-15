package com.alterjuice.utils.str

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes


sealed interface StrRes : Str {

    override fun transform(transformBlock: (String) -> String): Str = when (this) {
        is Transformable -> Transformable(this.str, this.transforms + transformBlock)
        else -> StrRes.Transformable(this, listOf(transformBlock))
    }
    interface ArgsHolder {
        val args: Array<out Any?>
    }

    class Plural(@PluralsRes val id: Int, val quantity: Int, override vararg val args: Any?) :
        StrRes, ArgsHolder

    class Text(@StringRes val id: Int, override vararg val args: Any?) : StrRes, ArgsHolder
    class Transformable internal constructor(
        internal val str: StrRes,
        internal val transforms: List<(String) -> String>
    ) : StrRes
}
