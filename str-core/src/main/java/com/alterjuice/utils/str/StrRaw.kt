package com.alterjuice.utils.str

sealed interface Raw : Str {
    override fun transform(transformBlock: (String) -> String): Str = when (this) {
        is Transformable -> Transformable(this.str, this.transforms + transformBlock)
        else -> Transformable(this, listOf(transformBlock))
    }
    class Text(val text: String) : Raw
    class Lambda(val block: () -> String) : Raw
    class Transformable internal constructor(
        internal val str: Raw,
        internal val transforms: List<(String) -> String>,
    ) : Raw
}

fun Raw.get(): String = when (this) {
    is Raw.Text -> this.text
    is Raw.Lambda -> this.block.invoke()
    is Raw.Transformable -> str.get().applyTransforms(transforms)
}


operator fun Str.Companion.invoke(block: () -> String) = Raw.Lambda(block)
operator fun Str.Companion.invoke(text: String) = Raw.Text(text)