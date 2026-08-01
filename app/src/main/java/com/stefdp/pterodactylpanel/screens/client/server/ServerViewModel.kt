package com.stefdp.pterodactylpanel.screens.client.server

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.getServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerUiState(
    val isLoading: Boolean = true,
    val server: GetServerResponse? = null,
    val currentTab: ServerTab = ServerTab.STARTUP, // TODO: set this back to CONSOLE
)

class ClientServerViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerUiState> = MutableStateFlow(ClientServerUiState())
    val state: StateFlow<ClientServerUiState> = _state.asStateFlow()

    private var serverId: String? = null

    fun init(
        context: Context,
        serverId: String,
        directory: String?,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            this@ClientServerViewModel.serverId = serverId

            if (directory != null) {
                _state.update {
                    it.copy(
                        currentTab = ServerTab.FILES,
                    )
                }
            }

            val serverRes = getServer(
                context = context,
                serverId = serverId
            )

            serverRes
                .onSuccess { server ->
                    _state.update {
                        it.copy(
                            server = server
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error("ClientServerViewModel", "Failed to fetch server data: ${error.message}")

                    onError("Failed to fetch server data: ${error.message}")
                }

            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun setCurrentTab(tab: ServerTab) {
        val isServerOwner = _state.value.server?.meta?.isServerOwner ?: false
        val userPermissions = _state.value.server?.meta?.userPermissions ?: emptyList()

        val permissions = ServerSubuser.Permissions.fromTab(tab)

        if (
            permissions != null &&
            !isServerOwner &&
            !userPermissions.any { permissions.contains(it) }
        ) return

        _state.update {
            it.copy(currentTab = tab)
        }
    }
}