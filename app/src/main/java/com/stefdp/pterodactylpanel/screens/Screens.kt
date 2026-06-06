package com.stefdp.pterodactylpanel.screens

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppScreen

@Serializable
object LoadingScreen : AppScreen

@Serializable
object LoginScreen : AppScreen

@Serializable
object HomeScreen : AppScreen