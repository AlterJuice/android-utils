package com.alterjuice.utils.str


/**
 * @see StrRaw
 * */
interface Str {
    fun transform(transformBlock: (String) -> String): Str
    fun append(text: String) = this.transform { it + text }
    fun prepend(text: String) = this.transform { text + it }
}


fun String.applyTransforms(transforms: List<(String) -> String>): String {
    return transforms.fold(this) { text, operation -> operation(text) }
}


fun String.asStr() = StrRaw(this)

