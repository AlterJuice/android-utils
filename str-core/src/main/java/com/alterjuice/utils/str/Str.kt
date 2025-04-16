package com.alterjuice.utils.str


/**
 * @see StrRaw
 * @see StrRes
 * */
interface Str {
    fun transform(transformBlock: (String) -> String): Str
    fun append(text: String) = this.transform { it + text }
    fun prepend(text: String) = this.transform { text + it }

    companion object {

        operator fun invoke(block: () -> String) = StrRaw.Lambda(block)
        operator fun invoke(text: String) = StrRaw.Text(text)
    }
}


fun String.applyTransforms(transforms: List<(String) -> String>): String {
    var temp = this
    transforms.forEach { transform -> temp = transform(temp) }
    return temp
}


fun String.toStr() = Str(this)


