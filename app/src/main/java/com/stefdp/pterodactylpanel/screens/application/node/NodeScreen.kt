package com.stefdp.pterodactylpanel.screens.application.node

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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.ScrollableTabRow
import com.stefdp.pterodactylpanel.components.Tab
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.application.node.tabs.about.AboutTab
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.AllocationsTab
import com.stefdp.pterodactylpanel.screens.application.node.tabs.configuration.ConfigurationTab
import com.stefdp.pterodactylpanel.screens.application.node.tabs.settings.SettingsTab

@Composable
fun ApplicationNodeScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    nodeId: Long,
    viewModel: ApplicationNodeViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

//    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
//        navController.navigate(LoginScreen) {
//            popUpTo(navController.graph.id) { inclusive = true }
//        }
//    }

    val state by viewModel.state.collectAsState()

    fun reload(isRefresh: Boolean = false) {
        viewModel.init(
            context = context,
            nodeId = nodeId,
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
            },
            isRefresh = isRefresh
        )
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    LaunchedEffect(nodeId) {
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
        val tabs = remember(
            localLoggedUser,
            state.currentTab,
            state.node
        ) {
            NodeTab.entries.map { nodeTab ->
                Tab(
                    label = nodeTab.label,
                    id = nodeTab.id,
                    active = nodeTab == state.currentTab
                )
            }
        }

        ScrollableTabRow(
            tabs = tabs,
            onTabClick = { tab ->
                viewModel.setCurrentTab(NodeTab.valueOf(tab.id.uppercase()))
            },
            enabled = state.node != null && !state.isLoading
        )

        var refreshIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                reload(
                    isRefresh = true
                )

                refreshIndex++
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .waterfallPadding()
            ) {
                saveableStateHolder.SaveableStateProvider(key = state.currentTab) {
                    when (state.currentTab) {
                        NodeTab.ABOUT -> {
                            AboutTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                node = state.node,
                                nodeConfiguration = state.nodeConfiguration,
                                refreshIndex = refreshIndex
                            )
                        }

                        NodeTab.SETTINGS -> {
                            SettingsTab(
                                context = context,
                                activity = activity,
                                node = state.node,
                                refreshIndex = refreshIndex
                            )
                        }

                        NodeTab.CONFIGURATION -> {
                            ConfigurationTab(
                                context = context,
                                activity = activity,
                                nodeConfiguration = state.nodeConfiguration,
                            )
                        }

                        NodeTab.ALLOCATIONS -> {
                            AllocationsTab(
                                navController = navController,
                                context = context,
                                activity = activity,
                                node = state.node,
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
}

enum class NodeTab(
    val id: String,
    val label: String
) {
    ABOUT(
        id = "about",
        label = "About"
    ),

    SETTINGS(
        id = "settings",
        label = "Settings"
    ),

    CONFIGURATION(
        id = "configuration",
        label = "Configuration"
    ),

    ALLOCATIONS(
        id = "allocations",
        label = "Allocations"
    ),

    SERVERS(
        id = "servers",
        label = "Servers"
    )
}