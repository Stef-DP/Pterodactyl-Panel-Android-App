package com.stefdp.pterodactylpanel.screens.client.server

import android.R.attr.text
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.ApplicationApiKeyValidity
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalApplicationApiKeyValidity
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.ScrollableTabRow
import com.stefdp.pterodactylpanel.components.Tab
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.BackupsTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.console.ConsoleTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.DatabasesTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.FilesTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.network.NetworkTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.SchedulesTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.settings.SettingsTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.startup.StartupTab
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.UsersTab

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

    Column {
        Logger.debug("ClientServerScreen", LocalApplicationApiKeyValidity.current.toString())

        val applicationApiKeyValidity = LocalApplicationApiKeyValidity.current
        val openInNewIcon = painterResource(R.drawable.open_in_new)

        LaunchedEffect(applicationApiKeyValidity) {
            Logger.debug("ClientServerScreen", "Application API key validity changed: $applicationApiKeyValidity")
        }

        val tabs by remember(
            applicationApiKeyValidity,
            state.currentTab,
            state.server,
        ) {
            mutableStateOf(
                value = (
                    ServerTab.entries.map { serverTab ->
                        val permissions = ServerSubuser.Permissions.fromTab(serverTab)

                        if (state.server == null) {
                            return@map null
                        }

                        if (
                            !state.server!!.meta.isServerOwner &&
                            permissions != null &&
                            state.server!!.meta.userPermissions.any { permissions.contains(it) }
                        ) {
                            return@map null
                        }

                        Tab(
                            label = serverTab.label,
                            id = serverTab.id,
                            active = serverTab == state.currentTab,
                        )
                    } + if (applicationApiKeyValidity == ApplicationApiKeyValidity.VALID) {
                        Tab(
                            icon = openInNewIcon,
                            iconContentDescription = "Open in admin view",
                            id = "admin",
                            active = false,
                            enabled = false // TODO: enabled when the admin side is done
                        )
                    } else null
                ).filterNotNull()
            )
        }

        ScrollableTabRow(
            tabs = tabs,
            onTabClick = { tab ->
                if (tab.id == "admin") {
                    // TODO: navigate to admin screen
                }

                viewModel.setCurrentTab(ServerTab.valueOf(tab.id.uppercase()))
            }
        )

        if (state.server?.attributes?.isSuspended == true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = 24.dp
                        )
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cloud_off),
                        contentDescription = "Suspended",
                        tint = MaterialTheme.colorScheme.surfaceDim,
                        modifier = Modifier
                            .requiredSize(60.dp)
                            .padding(
                                bottom = 8.dp
                            )
                    )

                    Text(
                        text = "Server Suspended",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.surfaceDim,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "This server is suspended and cannot be accessed",
                        color = MaterialTheme.colorScheme.surfaceDim,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        when (state.currentTab) {
            ServerTab.CONSOLE -> {
                ConsoleTab(
                    context = context,
                    activity = activity,
                    server = state.server
                )
            }

            ServerTab.FILES -> {
                FilesTab(
                    context = context,
                    activity = activity,
                    server = state.server,
                    directory = directory,
                )
            }

            ServerTab.DATABASES -> {
                DatabasesTab(
                    context = context,
                    activity = activity,
                    server = state.server
                )
            }

            ServerTab.SCHEDULES -> {
                SchedulesTab(
                    context = context,
                    activity = activity,
                    server = state.server
                )
            }

            ServerTab.USERS -> {
                UsersTab(
                    context = context,
                    activity = activity,
                    server = state.server
                )
            }

            ServerTab.BACKUPS -> {
                BackupsTab(
                    context = context,
                    activity = activity,
                    server = state.server
                )
            }

            ServerTab.NETWORK -> {
                NetworkTab(
                    context = context,
                    activity = activity,
                    server = state.server
                )
            }

            ServerTab.STARTUP -> {
                StartupTab(
                    context = context,
                    activity = activity,
                    server = state.server
                )
            }

            ServerTab.SETTINGS -> {
                SettingsTab(
                    context = context,
                    activity = activity,
                    server = state.server,
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