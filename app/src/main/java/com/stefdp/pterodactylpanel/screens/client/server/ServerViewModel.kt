package com.stefdp.pterodactylpanel.screens.client.server

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.Server
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.getServer
import com.stefdp.pterodactylpanel.utils.hasPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val server: GetServerResponse? = null,
    val currentTab: ServerTab = ServerTab.CONSOLE,
    val isSuspended: Boolean = false,
    val isInstalling: Boolean = false,
    val isTransferring: Boolean = false,
    val isNodeUnderMaintenance: Boolean = false,
    val isRestoringBackup: Boolean = false
)

class ClientServerViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerUiState> = MutableStateFlow(ClientServerUiState())
    val state: StateFlow<ClientServerUiState> = _state.asStateFlow()

    private var serverId: String? = null

    fun init(
        context: Context,
        serverId: String,
        directory: String?,
        isSuspended: Boolean = false,
        isInstalling: Boolean = false,
        isTransferring: Boolean = false,
        isNodeUnderMaintenance: Boolean = false,
        isRestoringBackup: Boolean = false,
        isServerOwner: Boolean = false,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            this@ClientServerViewModel.serverId = serverId

            _state.update {
                it.copy(
                    isLoading = true,
                    isSuspended = isSuspended,
                    isInstalling = isInstalling,
                    isTransferring = isTransferring,
                    isNodeUnderMaintenance = isNodeUnderMaintenance,
                    isRestoringBackup = isRestoringBackup,
                    isServerOwner = isServerOwner,
                )
            }

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
                            server = server,
                            isSuspended = server.attributes.isSuspended,
                            isInstalling = server.attributes.isInstalling,
                            isTransferring = server.attributes.isTransferring,
                            isNodeUnderMaintenance = server.attributes.isNodeUnderMaintenance,
                            isRestoringBackup = server.attributes.status == Server.Attributes.Status.RESTORING_BACKUP,
                            isServerOwner = server.meta.isServerOwner,
                            userPermissions = server.meta.userPermissions
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

        val hasPermission = hasPermission(
            isServerOwner = isServerOwner,
            userPermissions = userPermissions,
            requiredPermissions = permissions
        )

        if (
            permissions != null &&
            !hasPermission
        ) return

        _state.update {
            it.copy(currentTab = tab)
        }
    }
}