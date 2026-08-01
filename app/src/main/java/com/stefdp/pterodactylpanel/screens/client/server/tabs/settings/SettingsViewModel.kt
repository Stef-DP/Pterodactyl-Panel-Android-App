package com.stefdp.pterodactylpanel.screens.client.server.tabs.settings

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.reinstallServer
import com.stefdp.pterodactylpanel.network.client.requests.renameServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerSettingsTabUiState(
    val isLoading: Boolean = false,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val newServerName: TextFieldValue = TextFieldValue(""),
    val newServerDescription: TextFieldValue = TextFieldValue(""),
)

class ClientServerSettingsTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerSettingsTabUiState> = MutableStateFlow(ClientServerSettingsTabUiState())
    val state: StateFlow<ClientServerSettingsTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    fun init(server: GetServerResponse?) {
        this.serverId = server?.attributes?.identifier

        _state.update {
            it.copy(
                isServerOwner = server?.meta?.isServerOwner ?: false,
                userPermissions = server?.meta?.userPermissions ?: emptyList(),
                newServerName = TextFieldValue(server?.attributes?.name ?: ""),
                newServerDescription = TextFieldValue(server?.attributes?.description ?: "")
            )
        }
    }

    fun setNewServerName(newName: TextFieldValue) {
        _state.update {
            it.copy(newServerName = newName)
        }
    }

    fun setNewServerDescription(newDescription: TextFieldValue) {
        _state.update {
            it.copy(newServerDescription = newDescription)
        }
    }

    fun renameServer(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val renameServerRes = renameServer(
                context = context,
                serverId = serverId!!,
                name = state.value.newServerName.text,
                description = state.value.newServerDescription.text
            )

            renameServerRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSettingsTabViewModel", "Failed to rename server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to rename server: ${error.message}")
                }
        }
    }

    fun reinstall(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val reinstallServerRes = reinstallServer(
                context = context,
                serverId = serverId!!,
            )

            reinstallServerRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSettingsTabViewModel", "Failed to reinstall server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to rename server: ${error.message}")
                }
        }
    }
}