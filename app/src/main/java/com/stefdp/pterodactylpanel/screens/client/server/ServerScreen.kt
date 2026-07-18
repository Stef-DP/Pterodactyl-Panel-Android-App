package com.stefdp.pterodactylpanel.screens.client.server

import android.content.Context
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.ScrollableTabRow
import com.stefdp.pterodactylpanel.components.Tab
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.client.server.tabs.ConsoleTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.FilesTab
import com.stefdp.pterodactylpanel.utils.scrollbar

@Composable
fun ClientServerScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    serverId: String,
    directory: String? = null,
    viewModel: ClientServerViewModel = viewModel()
) {
    // TODO: uncomment this, it's just for debug
//    val localLoggedUser = LocalLoggedUser.current
//
//    if (localLoggedUser == null) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(serverId) {
        viewModel.init(
            context = context,
            serverId = serverId,
            directory = directory,
            onError = { error ->
                Notification.show(
                    activity = activity,
                    duration = 3000L
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }

    val scrollState = rememberScrollState()

    val disabledScrollScreen = listOf(
        ServerTab.FILES
    )

    Column(
        modifier = if (state.currentTab !in disabledScrollScreen) {
            Modifier
                .verticalScroll(scrollState)
                .scrollbar(
                    scrollState = scrollState,
                    direction = Orientation.Vertical
                )
            } else {
                Modifier
        }
    ) {
        val tabs = ServerTab.entries.map { serverTab ->
            Tab(
                label = serverTab.label,
                id = serverTab.id,
                active = serverTab == state.currentTab,
            )
        }

        ScrollableTabRow(
            tabs = tabs,
            onTabClick = { tab ->
                viewModel.setCurrentTab(ServerTab.valueOf(tab.id.uppercase()))
            }
        )

        when (state.currentTab) {
            ServerTab.CONSOLE -> {
                ConsoleTab(
                    navController = navController,
                    context = context,
                    activity = activity,
                    viewModel = viewModel,
                    state = state
                )
            }

            ServerTab.FILES -> {
                FilesTab(
                    navController = navController,
                    context = context,
                    activity = activity,
                    viewModel = viewModel,
                    state = state
                )
            }

             else -> {
                 Text("WIP")
             }
        }
    }
}

enum class WebSocketConnectionStatus {
    CONNECTING,
    CONNECTED,
    DISCONNECTED
}

enum class ServerTab(
    val id: String,
    val label: String
) {
    CONSOLE(
        id = "console",
        label = "Console"
    ),
    FILES(
        id = "files",
        label = "Files"
    ),
    DATABASES(
        id = "databases",
        label = "Databases"
    ),
    SCHEDULES(
        id = "schedules",
        label = "Schedules"
    ),
    USERS(
        id = "users",
        label = "Users"
    ),
    BACKUPS(
        id = "backups",
        label = "Backups"
    ),
    NETWORK(
        id = "network",
        label = "Network"
    ),
    STARTUP(
        id = "startup",
        label = "Startup"
    ),
    SETTINGS(
        id = "settings",
        label = "Settings"
    ),
    ACTIVITY(
        id = "activity",
        label = "Activity"
    )
}