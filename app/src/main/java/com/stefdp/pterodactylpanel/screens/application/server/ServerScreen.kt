package com.stefdp.pterodactylpanel.screens.application.server

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.ScrollableTabRow
import com.stefdp.pterodactylpanel.components.Tab
import com.stefdp.pterodactylpanel.screens.ClientServerScreen
import com.stefdp.pterodactylpanel.screens.application.server.tabs.about.AboutTab
import com.stefdp.pterodactylpanel.screens.application.server.tabs.buildconfiguration.BuildConfigurationTab
import com.stefdp.pterodactylpanel.screens.application.server.tabs.databases.DatabasesTab
import com.stefdp.pterodactylpanel.screens.application.server.tabs.details.DetailsTab
import com.stefdp.pterodactylpanel.screens.application.server.tabs.startup.StartupTab

@Composable
fun ApplicationServerScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    serverId: Long,
    viewModel: ApplicationServerViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }

    val state by viewModel.state.collectAsState()

    var refreshIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    fun reload(
        isRefresh: Boolean = false,
        onReloadFinish: () -> Unit = {},
        increaseRefreshIndex: Boolean = false,
        onError: (String) -> Unit = { error ->
            Notification.show(
                activity = activity,
                duration = 3000L
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
    ) {
        viewModel.init(
            context = context,
            serverId = serverId,
            onError = onError,
            onReloadFinish = {
                if (isRefresh || increaseRefreshIndex) refreshIndex++

                onReloadFinish()
            },
            isRefresh = isRefresh
        )
    }

    val saveableStateHolder = rememberSaveableStateHolder()

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
        val openInNewIcon = painterResource(R.drawable.open_in_new)

        val tabs = remember(
            localLoggedUser,
            state.currentTab,
            state.server
        ) {
            ServerTab.entries.map { serverTab ->
                Tab(
                    label = serverTab.label,
                    id = serverTab.id,
                    active = serverTab == state.currentTab
                )
            } + Tab(
                icon = openInNewIcon,
                iconContentDescription = "Open in user view",
                id = "user",
                active = state.server != null
            )
        }

        ScrollableTabRow(
            tabs = tabs,
            onTabClick = { tab ->
                if (tab.id == "user") {
                    navController.navigate(
                        ClientServerScreen(state.server!!.attributes.identifier)
                    )

                    return@ScrollableTabRow
                }

                viewModel.setCurrentTab(ServerTab.valueOf(tab.id.uppercase()))
            },
            enabled = state.server != null && !state.isLoading
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                reload(
                    isRefresh = true
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .waterfallPadding()
            ) {
                saveableStateHolder.SaveableStateProvider(key = state.currentTab) {
                    when (state.currentTab) {
                        ServerTab.ABOUT -> {
                            AboutTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                server = state.server,
                                refreshIndex = refreshIndex
                            )
                        }

                        ServerTab.DETAILS -> {
                            DetailsTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                server = state.server,
                                refreshIndex = refreshIndex
                            )
                        }

                        ServerTab.BUILD_CONFIGURATION -> {
                            BuildConfigurationTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                server = state.server,
                                refreshIndex = refreshIndex,
                                reload = ::reload
                            )
                        }

                        ServerTab.STARTUP -> {
                            StartupTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                server = state.server,
                                refreshIndex = refreshIndex
                            )
                        }

                        ServerTab.DATABASES -> {
                            DatabasesTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                server = state.server,
                                refreshIndex = refreshIndex,
                                reload = ::reload
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
}

enum class ServerTab(
    val id: String,
    val label: String
) {
    ABOUT(
        id = "about",
        label = "About"
    ),

    DETAILS(
        id = "details",
        label = "Details"
    ),

    BUILD_CONFIGURATION(
        id = "build_configuration",
        label = "Build Configuration"
    ),

    STARTUP(
        id = "startup",
        label = "Startup"
    ),

    DATABASES(
        id = "databases",
        label = "Databases"
    ),

    // i think no api endpoint return that
//    MOUNTS(
//        id = "mounts",
//        label = "Mounts"
//    ),

    MANAGE(
        id = "manage",
        label = "Manage"
    ),

    DELETE(
        id = "delete",
        label = "Delete"
    )
}