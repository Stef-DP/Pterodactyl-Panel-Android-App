package com.stefdp.pterodactylpanel.screens.application.server.tabs.buildconfiguration

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.application.models.ApplicationAllocation
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerBuildBody
import com.stefdp.pterodactylpanel.network.application.requests.getNode
import com.stefdp.pterodactylpanel.network.application.requests.updateServerBuild
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationServerBuildConfigurationTabUiState(
    val isLoading: Boolean = true,
    val cpuLimit: TextFieldValue = TextFieldValue(""),
    val cpuPinning: TextFieldValue = TextFieldValue(""),
    val allocatedMemory: TextFieldValue = TextFieldValue(""),
    val allocatedSwap: TextFieldValue = TextFieldValue(""),
    val diskSpaceLimit: TextFieldValue = TextFieldValue(""),
    val blockIoProportion: TextFieldValue = TextFieldValue(""),
    val oomKiller: Boolean = false,
    val databaseLimit: TextFieldValue = TextFieldValue(""),
    val allocationLimit: TextFieldValue = TextFieldValue(""),
    val backupLimit: TextFieldValue = TextFieldValue(""),
    val gamePort: Set<String> = emptySet(),
    val gamePorts: List<ApplicationAllocation> = emptyList(),
    val addNewPorts: Set<String> = emptySet(),
    val removePorts: Set<String> = emptySet(),
    val newAvailablePorts: List<ApplicationAllocation> = emptyList(),
)

private const val TAG = "ApplicationServerBuildConfigurationTabViewModel"

class ApplicationServerBuildConfigurationTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerBuildConfigurationTabUiState> = MutableStateFlow(ApplicationServerBuildConfigurationTabUiState())
    val state: StateFlow<ApplicationServerBuildConfigurationTabUiState> = _state.asStateFlow()

    private var server: ApplicationServer? = null

    fun init(
        context: Context,
        server: ApplicationServer?,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            this@ApplicationServerBuildConfigurationTabViewModel.server = server

            _state.update {
                it.copy(
                    cpuLimit = TextFieldValue(server?.attributes?.limits?.cpu?.toString() ?: ""),
                    cpuPinning = TextFieldValue(server?.attributes?.limits?.threads ?: ""),
                    allocatedMemory = TextFieldValue(server?.attributes?.limits?.memory?.toString() ?: ""),
                    allocatedSwap = TextFieldValue(server?.attributes?.limits?.swap?.toString() ?: ""),
                    diskSpaceLimit = TextFieldValue(server?.attributes?.limits?.disk?.toString() ?: ""),
                    blockIoProportion = TextFieldValue(server?.attributes?.limits?.io?.toString() ?: ""),
                    oomKiller = !(server?.attributes?.limits?.oomDisabled ?: true),
                    databaseLimit = TextFieldValue(server?.attributes?.featureLimits?.databases?.toString() ?: ""),
                    allocationLimit = TextFieldValue(server?.attributes?.featureLimits?.allocations?.toString() ?: ""),
                    backupLimit = TextFieldValue(server?.attributes?.featureLimits?.backups?.toString() ?: ""),
                    gamePort = server?.attributes?.relationships?.allocations?.data
                        ?.find { port -> port.attributes.id == server.attributes.allocation }
                        ?.let { port -> setOf(port.attributes.id.toString()) }
                        ?: emptySet(),
                    gamePorts = server?.attributes?.relationships?.allocations?.data ?: emptyList(),
                    addNewPorts = emptySet(),
                    removePorts = emptySet(),
                    newAvailablePorts = emptyList(),
                )
            }

            val nodeRes = getNode(
                context = context,
                nodeId = server?.attributes?.node ?: 1L,
                include = listOf(
                    ListNodesQueryInclude.ALLOCATIONS
                ).toQueryString()
            )

            nodeRes
                .onSuccess { node ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            newAvailablePorts = node.attributes.relationships?.allocations?.data ?: emptyList()
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch node allocations: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch node allocations: ${error.message}")
                }
        }
    }

    fun setCpuLimit(value: TextFieldValue) {
        _state.update {
            it.copy(cpuLimit = value)
        }
    }

    fun setCpuPinning(value: TextFieldValue) {
        _state.update {
            it.copy(cpuPinning = value)
        }
    }

    fun setAllocatedMemory(value: TextFieldValue) {
        _state.update {
            it.copy(allocatedMemory = value)
        }
    }

    fun setAllocatedSwap(value: TextFieldValue) {
        _state.update {
            it.copy(allocatedSwap = value)
        }
    }

    fun setDiskSpaceLimit(value: TextFieldValue) {
        _state.update {
            it.copy(diskSpaceLimit = value)
        }
    }

    fun setBlockIoProportion(value: TextFieldValue) {
        _state.update {
            it.copy(blockIoProportion = value)
        }
    }

    fun setOomKiller(value: Boolean) {
        _state.update {
            it.copy(oomKiller = value)
        }
    }

    fun setDatabaseLimit(value: TextFieldValue) {
        _state.update {
            it.copy(databaseLimit = value)
        }
    }

    fun setAllocationLimit(value: TextFieldValue) {
        _state.update {
            it.copy(allocationLimit = value)
        }
    }

    fun setBackupLimit(value: TextFieldValue) {
        _state.update {
            it.copy(backupLimit = value)
        }
    }

    fun setGamePort(value: Set<String>) {
        _state.update {
            it.copy(gamePort = value)
        }
    }

    fun setAddNewPorts(value: Set<String>) {
        _state.update {
            it.copy(addNewPorts = value)
        }
    }

    fun setRemovePorts(value: Set<String>) {
        _state.update {
            it.copy(removePorts = value)
        }
    }

    fun updateServer(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        reload: (
            isRefresh: Boolean,
            onReloadFinish: () -> Unit,
            increaseRefreshIndex: Boolean,
            onError: (String) -> Unit
        ) -> Unit
    ) {
        viewModelScope.launch {
            if (server == null) {
                onError("Missing Server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateServerRes = updateServerBuild(
                context = context,
                serverId = server!!.attributes.id,
                allocation = state.value.gamePort.firstOrNull()?.toLong() ?: server!!.attributes.allocation,
                featureLimits = UpdateServerBuildBody.FeatureLimits(
                    databases = state.value.databaseLimit.text.toLongOrNull() ?: server!!.attributes.featureLimits.databases,
                    allocations = state.value.allocationLimit.text.toLongOrNull() ?: server!!.attributes.featureLimits.allocations,
                    backups = state.value.backupLimit.text.toLongOrNull() ?: server!!.attributes.featureLimits.backups
                ),
                addAllocations = state.value.addNewPorts.mapNotNull { it.toLongOrNull() },
                removeAllocations = state.value.removePorts.mapNotNull { it.toLongOrNull() },
                oomDisabled = !state.value.oomKiller,
                cpu = state.value.cpuLimit.text.toLongOrNull() ?: server!!.attributes.limits.cpu,
                disk = state.value.diskSpaceLimit.text.toLongOrNull() ?: server!!.attributes.limits.disk,
                io = state.value.blockIoProportion.text.toLongOrNull() ?: server!!.attributes.limits.io,
                memory = state.value.allocatedMemory.text.toLongOrNull() ?: server!!.attributes.limits.memory,
                swap = state.value.allocatedSwap.text.toLongOrNull() ?: server!!.attributes.limits.swap,
                threads = state.value.cpuPinning.text.ifBlank {
                    server!!.attributes.limits.threads
                }
            )

            updateServerRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            addNewPorts = emptySet(),
                            removePorts = emptySet()
                        )
                    }

                    reload(
                        false,
                        {

                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        },
                        true,
                        { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }

                            onError(error)
                        }
                    )

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to update server build configuration: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server build configuration: ${error.message}")
                }
        }
    }
}