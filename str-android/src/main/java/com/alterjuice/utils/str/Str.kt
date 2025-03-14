package com.alterjuice.utils.str

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes


sealed interface Res : Str {

    override fun transform(transformBlock: (kotlin.String) -> kotlin.String): Str = when (this) {
        is Transformable -> Transformable(this.str, this.transforms + transformBlock)
        else -> Res.Transformable(this, listOf(transformBlock))
    }
    interface ArgsHolder {
        val args: Array<out Any?>
    }

    class Plural(@PluralsRes val id: Int, val quantity: Int, override vararg val args: Any?) :
        Res, ArgsHolder

    class String(@StringRes val id: Int, override vararg val args: Any?) : Res, ArgsHolder
    class Transformable internal constructor(
        internal val str: Res,
        internal val transforms: List<(kotlin.String) -> kotlin.String>
    ) : Res
}
