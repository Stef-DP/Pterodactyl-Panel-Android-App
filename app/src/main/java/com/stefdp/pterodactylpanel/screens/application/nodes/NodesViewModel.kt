package com.stefdp.pterodactylpanel.screens.application.nodes

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationLocation
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQuerySort
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNodesResponse
import com.stefdp.pterodactylpanel.network.application.requests.createNode
import com.stefdp.pterodactylpanel.network.application.requests.getNodeStatus
import com.stefdp.pterodactylpanel.network.application.requests.listLocations
import com.stefdp.pterodactylpanel.network.application.requests.listNodes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNodesUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val nodes: List<ApplicationNode>? = null,
    val locations: List<ApplicationLocation> = emptyList(),
    val page: Long = 1,
    val pagination: ListNodesResponse.Meta.Pagination? = null,
    val nodesStatus: Map<Long, Boolean?> = emptyMap(),
    val showCreateNodePopup: Boolean = false,
    val newNodeName: TextFieldValue = TextFieldValue(""),
    val newNodeDescription: TextFieldValue = TextFieldValue(""),
    val selectedNewNodeLocation: Set<String> = emptySet(),
    val newNodePublic: Boolean = true,
    val newNodeFQDN: TextFieldValue = TextFieldValue(""),
    val newNodeUseSsl: Boolean = true,
    val newNodeBehindProxy: Boolean = false,
    val newNodeDaemonServerFileDirectory: TextFieldValue = TextFieldValue("/var/lib/pterodactyl/volumes"),
    val newNodeTotalMemory: TextFieldValue = TextFieldValue(""),
    val newNodeMemoryOverallocation: TextFieldValue = TextFieldValue(""),
    val newNodeTotalDisk: TextFieldValue = TextFieldValue(""),
    val newNodeDiskOverallocation: TextFieldValue = TextFieldValue(""),
    val newNodeDaemonPort: TextFieldValue = TextFieldValue("8080"),
    val newNodeDaemonSftpPort: TextFieldValue = TextFieldValue("2022"),
)

private const val TAG = "ApplicationNodesViewModel"

class ApplicationNodesViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNodesUiState> = MutableStateFlow(ApplicationNodesUiState())
    val state: StateFlow<ApplicationNodesUiState> = _state.asStateFlow()

    fun updateData(
        context: Context,
        filterUuid: String? = null,
        filterName: String? = null,
        filterFQDN: String? = null,
        filterDaemonTokenId: String? = null,
        sort: ListNodesQuerySort? = null,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    nodes = null,
                    isRefreshing = isRefresh
                )
            }

            val nodesRes = listNodes(
                context = context,
                filterUuid = filterUuid,
                filterName = filterName,
                filterFQDN = filterFQDN,
                filterDaemonTokenId = filterDaemonTokenId,
                sort = sort,
                page = _state.value.page,
                include = ListNodesQueryInclude.toQueryString(
                    ListNodesQueryInclude.LOCATION,
                    ListNodesQueryInclude.SERVERS
                )
            )

            val nodes = nodesRes.getOrNull()

            val locations = listAllLocations(
                context = context
            )

            _state.update {
                it.copy(
                    nodes = nodes?.data ?: emptyList(),
                    locations = locations,
                    selectedNewNodeLocation = locations.firstOrNull()
                        ?.attributes
                        ?.id
                        ?.toString()
                        ?.let { id -> setOf(id) }
                        ?: emptySet(),
                    pagination = nodes?.meta?.pagination,
                    isRefreshing = false,
                    isLoading = false,
                    nodesStatus = nodes?.data?.associate { node ->
                        node.attributes.id to null
                    } ?: emptyMap(),
                )
            }

            updateNodesStatus(
                context = context
            )
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

    fun updateNodesStatus(
        context: Context
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            for (node in _state.value.nodes ?: emptyList()) {
                val nodeScheme = if (node.attributes.scheme == ApplicationNode.Attributes.Scheme.HTTPS)
                    "https"
                else
                    "http"

                val nodeFQDN = node.attributes.fqdn
                val nodePort = node.attributes.daemonListen

                val nodeBaseUrl = "$nodeScheme://$nodeFQDN:$nodePort"

                val status = getNodeStatus(
                    context = context,
                    url = "$nodeBaseUrl/api/system"
                )

                status
                    .onSuccess {
                        _state.update {
                            it.copy(
                                nodesStatus = it.nodesStatus + (node.attributes.id to true)
                            )
                        }
                    }
                    .onFailure {
                        _state.update {
                            it.copy(
                                nodesStatus = it.nodesStatus + (node.attributes.id to false)
                            )
                        }
                    }
            }

            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun setPage(page: Long) {
        if (page == _state.value.page) return

        _state.update {
            it.copy(
                nodes = null,
                page = page
            )
        }
    }

    fun showCreateNodePopup() {
        _state.update {
            it.copy(
                showCreateNodePopup = true
            )
        }
    }

    fun hideCreateNodePopup(
        skipLoading: Boolean = false
    ) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateNodePopup = false,
                newNodeName = TextFieldValue(""),
                newNodeDescription = TextFieldValue(""),
                selectedNewNodeLocation = emptySet(),
                newNodePublic = true,
                newNodeFQDN = TextFieldValue(""),
                newNodeUseSsl = true,
                newNodeBehindProxy = false,
                newNodeDaemonServerFileDirectory = TextFieldValue("/var/lib/pterodactyl/volumes"),
                newNodeTotalMemory = TextFieldValue(""),
                newNodeMemoryOverallocation = TextFieldValue(""),
                newNodeTotalDisk = TextFieldValue(""),
                newNodeDiskOverallocation = TextFieldValue(""),
                newNodeDaemonPort = TextFieldValue("8080"),
                newNodeDaemonSftpPort = TextFieldValue("2022")
            )
        }
    }

    fun setNewNodeName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeName = name
            )
        }
    }

    fun setNewNodeDescription(description: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeDescription = description
            )
        }
    }

    fun setSelectedNewNodeLocation(location: Set<String>) {
        _state.update {
            it.copy(
                selectedNewNodeLocation = location
            )
        }
    }

    fun setNewNodePublic(isPublic: Boolean) {
        _state.update {
            it.copy(
                newNodePublic = isPublic
            )
        }
    }

    fun setNewNodeFQDN(fqdn: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeFQDN = fqdn
            )
        }
    }

    fun setNewNodeUseSsl(useSsl: Boolean) {
        _state.update {
            it.copy(
                newNodeUseSsl = useSsl
            )
        }
    }

    fun setNewNodeBehindProxy(behindProxy: Boolean) {
        _state.update {
            it.copy(
                newNodeBehindProxy = behindProxy
            )
        }
    }

    fun setNewNodeDaemonServerFileDirectory(directory: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeDaemonServerFileDirectory = directory
            )
        }
    }

    fun setNewNodeTotalMemory(memory: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeTotalMemory = memory
            )
        }
    }

    fun setNewNodeMemoryOverallocation(memory: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeMemoryOverallocation = memory
            )
        }
    }

    fun setNewNodeTotalDisk(disk: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeTotalDisk = disk
            )
        }
    }

    fun setNewNodeDiskOverallocation(disk: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeDiskOverallocation = disk
            )
        }
    }

    fun setNewNodeDaemonPort(port: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeDaemonPort = port
            )
        }
    }

    fun setNewNodeDaemonSftpPort(port: TextFieldValue) {
        _state.update {
            it.copy(
                newNodeDaemonSftpPort = port
            )
        }
    }

    fun createNode(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val createNodeRes = createNode(
                context = context,
                daemonListen = _state.value.newNodeDaemonPort.text.trim().toIntOrNull() ?: 8080,
                daemonSftp = _state.value.newNodeDaemonSftpPort.text.trim().toIntOrNull() ?: 2022,
                disk = _state.value.newNodeTotalDisk.text.trim().toLongOrNull() ?: 0,
                diskOverallocate = _state.value.newNodeDiskOverallocation.text.trim().toLongOrNull() ?: 0,
                fqdn = _state.value.newNodeFQDN.text.trim(),
                locationId = _state.value.selectedNewNodeLocation.firstOrNull()?.toLongOrNull() ?: 0,
                memory = _state.value.newNodeTotalMemory.text.trim().toLongOrNull() ?: 0,
                memoryOverallocate = _state.value.newNodeMemoryOverallocation.text.trim().toLongOrNull() ?: 0,
                name = _state.value.newNodeName.text.trim(),
                scheme = if (_state.value.newNodeUseSsl)
                    ApplicationNode.Attributes.Scheme.HTTPS
                else
                    ApplicationNode.Attributes.Scheme.HTTP,
                behindProxy = _state.value.newNodeBehindProxy,
                daemonBase = _state.value.newNodeDaemonServerFileDirectory.text.trim(),
                description = _state.value.newNodeDescription.text.trim(),
                maintenanceMode = false,
                public = _state.value.newNodePublic,
            )

            createNodeRes
                .onSuccess {
                    updateData(
                        context = context
                    )

                    hideCreateNodePopup(true)

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to create node: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create node: ${error.message}")
                }
        }
    }
}