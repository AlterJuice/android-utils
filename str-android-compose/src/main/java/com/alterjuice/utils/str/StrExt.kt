package com.alterjuice.utils.str

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


@Composable
fun Str.get(): String {
    val context = LocalContext.current
    return remember(this) {
        this.get(context)
    }
}