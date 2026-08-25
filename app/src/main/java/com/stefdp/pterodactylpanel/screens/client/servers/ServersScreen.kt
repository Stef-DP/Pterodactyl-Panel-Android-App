package com.stefdp.pterodactylpanel.screens.client.servers

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.network.client.models.Server
import com.stefdp.pterodactylpanel.network.client.models.ServerStats
import com.stefdp.pterodactylpanel.screens.ClientServerScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.client.servers.components.ServerDisplay
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun ClientServersScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: ClientServersViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    fun updateData(
        filterName: String? = null,
        filterUuid: String? = null,
        filterExternalId: String? = null,
        filterDescription: String? = null,
        filterAny: String? = null,
        isRefresh: Boolean = false
    ) {
        viewModel.updateData(
            context = context,
            filterName = filterName,
            filterUuid = filterUuid,
            filterExternalId = filterExternalId,
            filterDescription = filterDescription,
            filterAny = filterAny,
            isRefresh = isRefresh
        )
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(state.page, state.showOtherServers) {
        updateData()
        scrollState.animateScrollTo(0)
    }

    val coroutineScope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                updateData(
                    isRefresh = true
                )
            }
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val isAdmin = localLoggedUser?.attributes?.admin == true

            if (isAdmin) {
                Switch(
                    checked = state.showOtherServers,
                    onCheckedChange = {
                        viewModel.setShowOtherServers(it)
                    },
                    label = if (state.showOtherServers) {
                        "Showing Others' Servers"
                    } else {
                        "Showing Your Servers"
                    },
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 12.dp,
                        bottom = 4.dp
                    ),
                    enabled = state.servers != null
                )
            }

            Column(
                modifier = Modifier
                    .verticalScrollWithScrollbar(
                        scrollState = scrollState,
                    )
                    .weight(1f)
                    .padding(
                        horizontal = 12.dp,
                    )
                    .padding(
                        top = if (isAdmin) 0.dp else 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.servers == null) {
                    repeat(10) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shimmerable(
                                    enabled = true,
                                    height = 160.dp,
                                )
                        ) {}
                    }

                    return@Column
                }

                state.servers?.let { servers ->
                    if (servers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "There are no servers to display",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        return@Column
                    }

                    for (server in servers) {
                        var serverStats by rememberSaveable {
                            mutableStateOf<ServerStats?>(null)
                        }

                        var serverStatsLoading by rememberSaveable {
                            mutableStateOf(true)
                        }

                        LaunchedEffect(Unit) {
                            if (server.attributes.isSuspended) {
                                serverStatsLoading = false

                                return@LaunchedEffect
                            }

                            val serverStatsRes = viewModel.getServerStats(
                                context = context,
                                serverId = server.attributes.identifier
                            )

                            serverStats = serverStatsRes
                            serverStatsLoading = false
                        }

                        ServerDisplay(
                            server = server,
                            serverStats = serverStats,
                            statsLoading = serverStatsLoading,
                            onOpen = {
                                navController.navigate(
                                    ClientServerScreen(
                                        serverId = server.attributes.identifier,
                                        isServerSuspended = server.attributes.isSuspended,
                                        isServerInstalling = server.attributes.isInstalling,
                                        isServerTransferring = server.attributes.isTransferring,
                                        isServerNodeUnderMaintenance = server.attributes.isNodeUnderMaintenance,
                                        isServerRestoringBackup = server.attributes.status == Server.Attributes.Status.RESTORING_BACKUP,
                                        isServerOwner = server.attributes.serverOwner,

                                        )
                                )
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Pager(
                currentPage = state.page,
                totalPages = state.pagination?.totalPages ?: 1,
                enabled = state.servers != null && !state.servers.isNullOrEmpty(),
                onFirstPageClick = {
                    viewModel.setPage(1)
                },
                onPreviousPageClick = {
                    viewModel.setPage(state.page - 1)
                },
                onCustomPageInput = { page ->
                    viewModel.setPage(page)
                },
                onNextPageClick = {
                    viewModel.setPage(state.page + 1)
                },
                onLastPageClick = {
                    viewModel.setPage(state.pagination?.totalPages ?: 1)
                },
                modifier = Modifier.padding(
                    horizontal = 12.dp
                )
            )
        }
    }
}