package com.stefdp.zipline

import androidx.compose.runtime.Composable
import com.stefdp.pterodactylpanel.screens.AppScreen
import com.stefdp.pterodactylpanel.screens.LoadingScreen

const val IS_DEBUG = false
const val DEBUG_NETWORK = false

val DEBUG_SCREEN: AppScreen = LoadingScreen

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {}

class Logger {
    companion object {
        fun debug(tag: String, message: String, throwable: Throwable? = null) {}

        fun error(tag: String, message: String, throwable: Throwable? = null) {}

        fun info(tag: String, message: String, throwable: Throwable? = null) {}

        fun warn(tag: String, message: String, throwable: Throwable? = null) {}

        fun verbose(tag: String, message: String, throwable: Throwable? = null) {}

        fun wtf(tag: String, message: String, throwable: Throwable? = null) {}
    }
}