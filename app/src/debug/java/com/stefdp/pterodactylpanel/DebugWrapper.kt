package com.stefdp.pterodactylpanel

import android.util.Log
import androidx.compose.runtime.Composable
import com.stefdp.pterodactylpanel.screens.AppScreen
import com.stefdp.pterodactylpanel.screens.AccountSettingsScreen
import com.stefdp.pterodactylpanel.screens.ApplicationLocationsScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNodeScreen
import com.stefdp.pterodactylpanel.screens.ApplicationNodesScreen
import com.stefdp.pterodactylpanel.screens.LoadingScreen

const val IS_DEBUG = true
const val DEBUG_NETWORK = false

val DEBUG_SCREEN: AppScreen = LoadingScreen//ApplicationNodeScreen(1)//LoadingScreen

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}

class Logger {
    companion object {
        fun debug(tag: String, message: String, throwable: Throwable? = null) {
            Log.d(tag, message, throwable)
        }

        fun error(tag: String, message: String, throwable: Throwable? = null) {
            Log.e(tag, message, throwable)
        }

        fun info(tag: String, message: String, throwable: Throwable? = null) {
            Log.i(tag, message, throwable)
        }

        fun warn(tag: String, message: String, throwable: Throwable? = null) {
            Log.w(tag, message, throwable)
        }

        fun verbose(tag: String, message: String, throwable: Throwable? = null) {
            Log.v(tag, message, throwable)
        }

        fun wtf(tag: String, message: String, throwable: Throwable? = null) {
            Log.wtf(tag, message, throwable)
        }
    }
}