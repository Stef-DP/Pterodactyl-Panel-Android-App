package com.stefdp.pterodactylpanel.screens.client.server

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
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
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
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
import com.stefdp.pterodactylpanel.utils.hasPermission

@Composable
fun ClientServerScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    serverId: String,
    directory: String? = null,
    isSuspended: Boolean = false,
    isInstalling: Boolean = false,
    isTransferring: Boolean = false,
    isNodeUnderMaintenance: Boolean = false,
    isRestoringBackup: Boolean = false,
    isServerOwner: Boolean = false,
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

    fun reload() {
        viewModel.init(
            context = context,
            serverId = serverId,
            isSuspended = isSuspended,
            isInstalling = isInstalling,
            isTransferring = isTransferring,
            isNodeUnderMaintenance = isNodeUnderMaintenance,
            isRestoringBackup = isRestoringBackup,
            isServerOwner = isServerOwner,
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

    LaunchedEffect(serverId) {
        reload()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
    ) {
        val applicationApiKeyValidity = LocalApplicationApiKeyValidity.current
        val openInNewIcon = painterResource(R.drawable.open_in_new)

        val tabs by remember(
            applicationApiKeyValidity,
            state.currentTab,
            state.server,
        ) {
            mutableStateOf(
                value = (
                    ServerTab.entries.map { serverTab ->
                        val permissions = ServerSubuser.Permissions.fromTab(serverTab)

                        val tab = Tab(
                            label = serverTab.label,
                            id = serverTab.id,
                            active = serverTab == state.currentTab,
                        )

                        if (
                            state.server == null ||
                            (
                                state.isLoading && state.userPermissions.isEmpty()
                            )
                        ) {
                            if (tab.id == ServerTab.CONSOLE.id) return@map tab
                            return@map null
                        }

                        if (
                            !hasPermission(
                                isServerOwner = state.isServerOwner,
                                userPermissions = state.userPermissions,
                                requiredPermissions = permissions
                            )
                        ) {
                            return@map null
                        }

                        return@map tab
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
            },
            enabled = state.server != null && !state.isLoading
        )

        var refreshIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                reload()

                refreshIndex += 1
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    )
            ) {
                if (state.isSuspended) {
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

                    return@Column
                }

                if (state.isNodeUnderMaintenance) {
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
                                text = "Node under Maintenance",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "The node of this server is currently under maintenance",
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    return@Column
                }

                if (state.isInstalling) {
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
                                text = "Running Installer",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Your server should be ready soon, please try again in a few minutes",
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    return@Column
                }

                if (state.isRestoringBackup) {
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
                                text = "Restoring from Backup",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Your server is currently being restored from a backup, please check back in a few minutes",
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    return@Column
                }

                if (state.isTransferring) {
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
                                text = "Transferring",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Your server is being transferred to a new node, please check back later",
                                color = MaterialTheme.colorScheme.surfaceDim,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    return@Column
                }

                when (state.currentTab) {
                    ServerTab.CONSOLE -> {
                        ConsoleTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.FILES -> {
                        FilesTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            directory = directory,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.DATABASES -> {
                        DatabasesTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.SCHEDULES -> {
                        SchedulesTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.USERS -> {
                        UsersTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.BACKUPS -> {
                        BackupsTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.NETWORK -> {
                        NetworkTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.STARTUP -> {
                        StartupTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    ServerTab.SETTINGS -> {
                        SettingsTab(
                            context = context,
                            activity = activity,
                            server = state.server,
                            refreshIndex = refreshIndex
                        )
                    }

                    else -> {
                        Text("WIP")
                    }
                }
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