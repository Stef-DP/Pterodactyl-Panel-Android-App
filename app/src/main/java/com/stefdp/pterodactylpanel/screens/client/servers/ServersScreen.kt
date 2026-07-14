package com.stefdp.pterodactylpanel.screens.client.servers

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.network.client.models.ServerStats
import com.stefdp.pterodactylpanel.network.client.models.requests.GetServersQueryType
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.client.servers.components.ServerDisplay

@Composable
fun ClientServersScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
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
        type: GetServersQueryType = GetServersQueryType.OWNER,
    ) {
        viewModel.updateData(
            context = context,
            filterName = filterName,
            filterUuid = filterUuid,
            filterExternalId = filterExternalId,
            filterDescription = filterDescription,
            filterAny = filterAny,
            type = type,
        )
    }

    LaunchedEffect(Unit) {
        updateData()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp
            )

    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.servers.isNullOrEmpty()) {
                Text(
                    text = "There are no servers to display"
                )
            } else {
                state.servers?.forEach { server ->
                    var serverStats by remember {
                        mutableStateOf<ServerStats?>(null)
                    }

                    var serverStatsLoading by remember {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(Unit) {
                        val serverStatsRes = viewModel.getServerStats(
                            context = context,
                            serverId = server.attributes.identifier
                        )

                        serverStats = serverStatsRes
                        serverStatsLoading = false
                    }

                    ServerDisplay(
                        context = context,
                        server = server,
                        serverStats = serverStats,
                        statsLoading = serverStatsLoading,
                        onOpen = {

                        }
                    )
                }
            }
        }
    }
}