package com.stefdp.pterodactylpanel.screens.client.server.tabs.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerAllocation
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.assignAutomaticAllocationToServer
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerAllocation
import com.stefdp.pterodactylpanel.network.client.requests.listServerAllocations
import com.stefdp.pterodactylpanel.network.client.requests.setServerPrimaryAllocation
import com.stefdp.pterodactylpanel.network.client.requests.updateServerAllocationNotes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerNetworkTabUiState(
    val isLoading: Boolean = false,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val allocations: List<ServerAllocation> = emptyList(),
    val allocationToDelete: Long? = null
)

class ClientServerNetworkTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerNetworkTabUiState> = MutableStateFlow(ClientServerNetworkTabUiState())
    val state: StateFlow<ClientServerNetworkTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    fun init(server: GetServerResponse?) {
        serverId = server?.attributes?.identifier

        _state.update {
            it.copy(
                isServerOwner = server?.meta?.isServerOwner ?: false,
                userPermissions = server?.meta?.userPermissions ?: emptyList()
            )
        }
    }

    fun updateAllocations(
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

            val listAllocationsRes = listServerAllocations(
                context = context,
                serverId = serverId!!,
            )

            listAllocationsRes
                .onSuccess { allocations ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allocations = allocations.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerAllocationsTabViewModel", "Failed to fetch server allocations: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server allocations: ${error.message}")
                }
        }
    }

    fun setAllocationToDelete(
        allocationId: Long?,
        skipLoading: Boolean = false
    ) {
        if (allocationId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                allocationToDelete = allocationId,
            )
        }
    }

    fun deleteAllocation(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val allocationId = _state.value.allocationToDelete

            if (allocationId == null) {
                onError("Missing allocation ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteAllocationRes = deleteServerAllocation(
                context = context,
                serverId = serverId!!,
                allocationId = allocationId
            )

            deleteAllocationRes
                .onSuccess {
                    updateAllocations(
                        context = context,
                        onSuccess = {
                            setAllocationToDelete(null, true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerAllocationsTabViewModel", "Failed to delete server allocation: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete server allocation: ${error.message}")
                }
        }
    }

    fun makeAllocationPrimary(
        context: Context,
        allocationId: Long,
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

            val makePrimaryRes = setServerPrimaryAllocation(
                context = context,
                serverId = serverId!!,
                allocationId = allocationId
            )

            makePrimaryRes
                .onSuccess {
                    updateAllocations(
                        context = context,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerAllocationsTabViewModel", "Failed to make server allocation primary: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to make server allocation primary: ${error.message}")
                }
        }
    }

    fun updateAllocationNotes(
        context: Context,
        allocationId: Long,
        notes: String,
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

            val updateAllocationNotesRes = updateServerAllocationNotes(
                context = context,
                serverId = serverId!!,
                allocationId = allocationId,
                notes = notes
            )

            updateAllocationNotesRes
                .onSuccess {
                    updateAllocations(
                        context = context,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerAllocationsTabViewModel", "Failed to update server allocation notes: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server allocation notes: ${error.message}")
                }
        }
    }

    fun createAllocation(
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

            val assignAllocationRes = assignAutomaticAllocationToServer(
                context = context,
                serverId = serverId!!,
            )

            assignAllocationRes
                .onSuccess {
                    updateAllocations(
                        context = context,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerAllocationsTabViewModel", "Failed to create server allocation: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create server allocation: ${error.message}")
                }
        }
    }
}