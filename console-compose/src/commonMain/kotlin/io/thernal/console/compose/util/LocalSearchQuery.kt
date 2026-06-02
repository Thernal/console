package io.thernal.console.compose.util

import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

val LocalSearchQuery = compositionLocalOf<State<String>> { mutableStateOf("") }
