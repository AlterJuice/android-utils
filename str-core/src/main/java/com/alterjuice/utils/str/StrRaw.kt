package com.alterjuice.utils.str

sealed interface StrRaw : Str {
    data class Text(val text: String) : StrRaw
    data class Lambda(val block: () -> String) : StrRaw
    class Transformable internal constructor(
        internal val str: StrRaw,
        internal val transforms: List<(String) -> String>,
    ) : StrRaw

    fun get(): String = when (this) {
        is Text -> this.text
        is Lambda -> this.block.invoke()
        is Transformable -> str.get().applyTransforms(transforms)
    }


    override fun transform(transformBlock: (String) -> String): Str = when (this) {
        is Transformable -> Transformable(this.str, this.transforms + transformBlock)
        else -> Transformable(this, listOf(transformBlock))
    }


    companion object {
        operator fun invoke(block: () -> String) = Lambda(block)
        operator fun invoke(text: String) = Text(text)
    }
}