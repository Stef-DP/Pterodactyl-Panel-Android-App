package com.stefdp.pterodactylpanel.screens.application.node.tabs.settings

import android.R.attr.scheme
import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationLocation
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.requests.listLocations
import com.stefdp.pterodactylpanel.network.application.requests.updateNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNodeSettingsTabUiState(
    val isLoading: Boolean = true,
    val locations: List<ApplicationLocation> = emptyList(),
    val nodeName: TextFieldValue = TextFieldValue(""),
    val nodeDescription: TextFieldValue = TextFieldValue(""),
    val selectedNodeLocation: Set<String> = emptySet(),
    val nodePublic: Boolean = false,
    val nodeFQDN: TextFieldValue = TextFieldValue(""),
    val nodeUseSsl: Boolean = false,
    val nodeBehindProxy: Boolean = false,
    val nodeUnderMaintenance: Boolean = false,
    val nodeTotalMemory: TextFieldValue = TextFieldValue(""),
    val nodeMemoryOverallocation: TextFieldValue = TextFieldValue(""),
    val nodeTotalDisk: TextFieldValue = TextFieldValue(""),
    val nodeDiskOverallocation: TextFieldValue = TextFieldValue(""),
    val nodeMaxWebUploadFileSize: TextFieldValue = TextFieldValue(""),
    val nodeDaemonPort: TextFieldValue = TextFieldValue(""),
    val nodeDaemonSftpPort: TextFieldValue = TextFieldValue(""),
)

private const val TAG = "ApplicationNodeSettingsTabViewModel"

class ApplicationNodeSettingsTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNodeSettingsTabUiState> = MutableStateFlow(ApplicationNodeSettingsTabUiState())
    val state: StateFlow<ApplicationNodeSettingsTabUiState> = _state.asStateFlow()

    private var node: ApplicationNode? = null

    fun init(
        context: Context,
        node: ApplicationNode?,
    ) {
        viewModelScope.launch {
            this@ApplicationNodeSettingsTabViewModel.node = node

            _state.update {
                it.copy(
                    isLoading = true,
                    nodeName = TextFieldValue(node?.attributes?.name ?: ""),
                    nodeDescription = TextFieldValue(node?.attributes?.description ?: ""),
                    selectedNodeLocation = node?.attributes?.locationId?.let { id -> setOf(id.toString()) } ?: emptySet(),
                    nodePublic = node?.attributes?.public ?: true,
                    nodeFQDN = TextFieldValue(node?.attributes?.fqdn ?: ""),
                    nodeUseSsl = node?.attributes?.scheme == ApplicationNode.Attributes.Scheme.HTTPS,
                    nodeBehindProxy = node?.attributes?.behindProxy ?: false,
                    nodeUnderMaintenance = node?.attributes?.maintenanceMode ?: false,
                    nodeTotalMemory = TextFieldValue(node?.attributes?.memory?.toString() ?: ""),
                    nodeMemoryOverallocation = TextFieldValue(node?.attributes?.memoryOverallocate?.toString() ?: ""),
                    nodeTotalDisk = TextFieldValue(node?.attributes?.disk?.toString() ?: ""),
                    nodeDiskOverallocation = TextFieldValue(node?.attributes?.diskOverallocate?.toString() ?: ""),
                    nodeMaxWebUploadFileSize = TextFieldValue(node?.attributes?.uploadSize?.toString() ?: ""),
                    nodeDaemonPort = TextFieldValue(node?.attributes?.daemonListen?.toString() ?: ""),
                    nodeDaemonSftpPort = TextFieldValue(node?.attributes?.daemonSftp?.toString() ?: "")
                )
            }

            val locations = listAllLocations(context)

            _state.update {
                it.copy(
                    isLoading = false,
                    locations = locations
                )
            }
        }
    }

    suspend fun listAllLocations(
        context: Context
    ): List<ApplicationLocation> {
        val outputLocations = mutableListOf<ApplicationLocation>()

        var currentPage = 1L
        var hasNextPage = true

        while (hasNextPage) {
            val locationsRes = listLocations(
                context = context,
                page = currentPage
            )

            if (locationsRes.isFailure) break

            val locations = locationsRes.getOrNull() ?: break
            outputLocations.addAll(locations.data)

            val nextLink = locations.meta.pagination.links.next

            if (!nextLink.isNullOrEmpty()) {
                currentPage++
            } else {
                hasNextPage = false
            }
        }

        return outputLocations
    }

    fun updateNode(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (node == null) {
                onError("Missing Node ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateNodeRes = updateNode(
                context = context,
                nodeId = node!!.attributes.id,
                name = _state.value.nodeName.text.trim(),
                description = _state.value.nodeDescription.text.trim(),
                locationId = _state.value.selectedNodeLocation
                    .firstOrNull()
                    ?.toLongOrNull() ?: node!!.attributes.locationId,
                public = _state.value.nodePublic,
                fqdn = _state.value.nodeFQDN.text.trim(),
                scheme = if (_state.value.nodeUseSsl)
                    ApplicationNode.Attributes.Scheme.HTTPS
                else
                    ApplicationNode.Attributes.Scheme.HTTP,
                behindProxy = _state.value.nodeBehindProxy,
                maintenanceMode = _state.value.nodeUnderMaintenance,
                memory = _state.value.nodeTotalMemory.text.trim().toLongOrNull() ?: node!!.attributes.memory,
                memoryOverallocate = _state.value.nodeMemoryOverallocation.text.trim().toLongOrNull() ?: node!!.attributes.memoryOverallocate,
                disk = _state.value.nodeTotalDisk.text.trim().toLongOrNull() ?: node!!.attributes.disk,
                diskOverallocate = _state.value.nodeDiskOverallocation.text.trim().toLongOrNull() ?: node!!.attributes.diskOverallocate,
                uploadSize = _state.value.nodeMaxWebUploadFileSize.text.trim().toLongOrNull(),
                daemonListen = _state.value.nodeDaemonPort.text.trim().toIntOrNull() ?: node!!.attributes.daemonListen,
                daemonSftp = _state.value.nodeDaemonSftpPort.text.trim().toIntOrNull() ?: node!!.attributes.daemonSftp,
            )

            updateNodeRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to update node: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError(error.message ?: "Failed to update node")
                }
        }
    }

    fun setNodeName(name: TextFieldValue) {
        _state.update {
            it.copy(
                nodeName = name
            )
        }
    }

    fun setNodeDescription(description: TextFieldValue) {
        _state.update {
            it.copy(
                nodeDescription = description
            )
        }
    }

    fun setSelectedNodeLocation(location: Set<String>) {
        _state.update {
            it.copy(
                selectedNodeLocation = location
            )
        }
    }

    fun setNodePublic(isPublic: Boolean) {
        _state.update {
            it.copy(
                nodePublic = isPublic
            )
        }
    }

    fun setNodeFQDN(fqdn: TextFieldValue) {
        _state.update {
            it.copy(
                nodeFQDN = fqdn
            )
        }
    }

    fun setNodeUseSsl(useSsl: Boolean) {
        _state.update {
            it.copy(
                nodeUseSsl = useSsl
            )
        }
    }

    fun setNodeBehindProxy(behindProxy: Boolean) {
        _state.update {
            it.copy(
                nodeBehindProxy = behindProxy
            )
        }
    }

    fun setNodeUnderMaintenance(underMaintenance: Boolean) {
        _state.update {
            it.copy(
                nodeUnderMaintenance = underMaintenance
            )
        }
    }


    fun setNodeTotalMemory(memory: TextFieldValue) {
        _state.update {
            it.copy(
                nodeTotalMemory = memory
            )
        }
    }

    fun setNodeMemoryOverallocation(memoryOverallocation: TextFieldValue) {
        _state.update {
            it.copy(
                nodeMemoryOverallocation = memoryOverallocation
            )
        }
    }

    fun setNodeTotalDisk(disk: TextFieldValue) {
        _state.update {
            it.copy(
                nodeTotalDisk = disk
            )
        }
    }

    fun setNodeDiskOverallocation(diskOverallocation: TextFieldValue) {
        _state.update {
            it.copy(
                nodeDiskOverallocation = diskOverallocation
            )
        }
    }

    fun setNodeMaxWebUploadFileSize(uploadSize: TextFieldValue) {
        _state.update {
            it.copy(
                nodeMaxWebUploadFileSize = uploadSize
            )
        }
    }

    fun setNodeDaemonPort(port: TextFieldValue) {
        _state.update {
            it.copy(
                nodeDaemonPort = port
            )
        }
    }

    fun setNodeDaemonSftpPort(port: TextFieldValue) {
        _state.update {
            it.copy(
                nodeDaemonSftpPort = port
            )
        }
    }
}