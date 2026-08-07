package com.stefdp.pterodactylpanel.screens.client.server.tabs.startup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerEggVariable
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.getServerStartupVariables
import com.stefdp.pterodactylpanel.network.client.requests.updateServerDockerImage
import com.stefdp.pterodactylpanel.network.client.requests.updateServerStartupVariable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerStartupTabUiState(
    val isLoading: Boolean = false,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val variables: List<ServerEggVariable> = emptyList(),
    val startupCommand: String = "Loading...",
    val dockerImages: Map<String, String> = emptyMap(),
    val selectedDockerImage: Set<String> = emptySet(),
)

private const val TAG = "ClientServerStartupTabViewModel"

class ClientServerStartupTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerStartupTabUiState> = MutableStateFlow(ClientServerStartupTabUiState())
    val state: StateFlow<ClientServerStartupTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    fun init(server: GetServerResponse?) {
        serverId = server?.attributes?.identifier

        Logger.debug(TAG,
            "Variables: " + (server?.attributes?.relationships?.variables?.data?.joinToString(", ") { it.attributes.name } ?: "No variables found"))

        _state.update {
            it.copy(
                isServerOwner = server?.meta?.isServerOwner ?: false,
                userPermissions = server?.meta?.userPermissions ?: emptyList(),
                variables = server?.attributes?.relationships?.variables?.data ?: emptyList(),
                startupCommand = server?.attributes?.invocation ?: "",
                selectedDockerImage = server?.attributes?.dockerImage?.let { image -> setOf(image) } ?: emptySet()
            )
        }
    }

    fun updateStartup(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
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

            val getStartupRes = getServerStartupVariables(
                context = context,
                serverId = serverId!!,
            )

            getStartupRes
                .onSuccess { startup ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            variables = startup.data,
                            startupCommand = startup.meta.startupCommand,
                            dockerImages = startup.meta.dockerImages,
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug(TAG, "Failed to fetch server startup: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server startup: ${error.message}")
                }
        }
    }

    fun updateDockerImage(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        image: Set<String>
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

            val updateDockerImageRes = updateServerDockerImage(
                context = context,
                serverId = serverId!!,
                dockerImage = image.firstOrNull() ?: ""
            )

            updateDockerImageRes
                .onSuccess {
                    updateStartup(
                        context = context,
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    selectedDockerImage = image
                                )
                            }

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug(TAG, "Failed to update server docker image: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server docker image: ${error.message}")
                }
        }
    }

    fun updateVariable(
        context: Context,
        variable: String,
        value: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
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

            val updateVariableRes = updateServerStartupVariable(
                context = context,
                serverId = serverId!!,
                key = variable,
                value = value
            )

            updateVariableRes
                .onSuccess {
                    updateStartup(
                        context = context,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug(TAG, "Failed to update server variable: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server variable: ${error.message}")
                }
        }
    }
}