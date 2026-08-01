package com.stefdp.pterodactylpanel.screens

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppScreen

@Serializable
object LoadingScreen : AppScreen

@Serializable
object LoginScreen : AppScreen

@Serializable
object ClientServersScreen : AppScreen

@Serializable
object ClientAccountSettingsScreen : AppScreen

@Serializable
data class ClientServerScreen(
    val serverId: String,
    val directory: String? = null,
    val isServerSuspended: Boolean = false,
    val isServerInstalling: Boolean = false,
    val isServerTransferring: Boolean = false,
    val isServerNodeUnderMaintenance: Boolean = false,
    val isServerRestoringBackup: Boolean = false,
    val isServerOwner: Boolean = false,
) : AppScreen

@Serializable
object ApplicationLocationsScreen : AppScreen

@Serializable
data class ApplicationLocationScreen(val locationId: Long) : AppScreen

@Serializable
object ApplicationNodesScreen : AppScreen

@Serializable
data class ApplicationNodeScreen(val nodeId: Long) : AppScreen

@Serializable
object ApplicationServersScreen : AppScreen

@Serializable
data class ApplicationServerScreen(val serverId: Long) : AppScreen

@Serializable
object ApplicationUsersScreen : AppScreen

@Serializable
data class ApplicationUserScreen(val userId: Long) : AppScreen

@Serializable
object ApplicationNestsScreen : AppScreen

@Serializable
data class ApplicationNestScreen(val nestId: Long) : AppScreen

@Serializable
data class ApplicationNestEggScreen(val nestId: Long, val eggId: Long) : AppScreen