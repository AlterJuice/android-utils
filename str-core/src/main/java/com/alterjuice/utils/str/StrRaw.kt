package com.alterjuice.utils.str

sealed interface StrRaw : Str {
    override fun transform(transformBlock: (String) -> String): Str = when (this) {
        is Transformable -> Transformable(this.str, this.transforms + transformBlock)
        else -> Transformable(this, listOf(transformBlock))
    }
    class Text(val text: String) : StrRaw
    class Lambda(val block: () -> String) : StrRaw
    class Transformable internal constructor(
        internal val str: StrRaw,
        internal val transforms: List<(String) -> String>,
    ) : StrRaw

    companion object {
        operator fun invoke(block: () -> String) = StrRaw.Lambda(block)
        operator fun invoke(text: String) = StrRaw.Text(text)
    }
}

fun StrRaw.get(): String = when (this) {
    is StrRaw.Text -> this.text
    is StrRaw.Lambda -> this.block.invoke()
    is StrRaw.Transformable -> str.get().applyTransforms(transforms)
}
