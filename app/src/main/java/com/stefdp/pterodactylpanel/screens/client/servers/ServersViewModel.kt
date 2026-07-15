package com.stefdp.pterodactylpanel.screens.client.servers

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.Server
import com.stefdp.pterodactylpanel.network.client.models.ServerStats
import com.stefdp.pterodactylpanel.network.client.models.requests.GetServersQueryType
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServersMetaPagination
import com.stefdp.pterodactylpanel.network.client.requests.getServerResources
import com.stefdp.pterodactylpanel.network.client.requests.listServers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServersUiState(
    val servers: List<Server>? = null,
    val pagination: GetServersMetaPagination? = null,
    val page: Long = 1
)

class ClientServersViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServersUiState> = MutableStateFlow(ClientServersUiState())
    val state: StateFlow<ClientServersUiState> = _state.asStateFlow()

    fun updateData(
        context: Context,
        filterName: String? = null,
        filterUuid: String? = null,
        filterExternalId: String? = null,
        filterDescription: String? = null,
        filterAny: String? = null,
        type: GetServersQueryType = GetServersQueryType.OWNER,
        page: Long? = null
    )  {
        viewModelScope.launch {
            val serversRes = listServers(
                context = context,
                filterName = filterName,
                filterUuid = filterUuid,
                filterExternalId = filterExternalId,
                filterDescription = filterDescription,
                filterAny = filterAny,
                type = type,
                perPage = 10,
                page = page
            )

            val servers = serversRes.getOrNull()

            Logger.debug("updateData", "Fetched ${servers?.data?.size ?: 0} servers")

            _state.update {
                it.copy(
                    servers = servers?.data ?: emptyList(),
                    pagination = servers?.meta?.pagination
                )
            }
        }
    }

    suspend fun getServerStats(
        context: Context,
        serverId: String
    ): ServerStats? {
        val serverStatsRes = getServerResources(
            context = context,
            serverId = serverId
        )

        if (serverStatsRes.isSuccess) {
            return serverStatsRes.getOrNull()
        }

        return null
    }

    fun setPage(page: Long) {
        _state.update {
            it.copy(
                servers = null,
                page = page
            )
        }
    }
}