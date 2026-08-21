package com.stefdp.pterodactylpanel.screens.application.servers

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNest
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServerVariable
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateServerBody
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestEggsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServersQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServersQuerySort
import com.stefdp.pterodactylpanel.network.application.models.responses.ListServersResponse
import com.stefdp.pterodactylpanel.network.application.requests.createServer
import com.stefdp.pterodactylpanel.network.application.requests.listNests
import com.stefdp.pterodactylpanel.network.application.requests.listNodes
import com.stefdp.pterodactylpanel.network.application.requests.listServers
import com.stefdp.pterodactylpanel.network.application.requests.listUsers
import com.stefdp.pterodactylpanel.network.models.plus
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class ApplicationServersUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val servers: List<ApplicationServer>? = null,
    val page: Long = 1L,
    val pagination: ListServersResponse.Meta.Pagination? = null,
    val showCreateServerPopup: Boolean = false,
    val nests: List<ApplicationNest> = emptyList(),
    val nestsLoading: Boolean = false,
    val nodes: List<ApplicationNode> = emptyList(),
    val nodesLoading: Boolean = false,
    val newServerOwnerSuggestions: List<ApplicationUser> = emptyList(),
    val newServerOwnerSuggestionsLoading: Boolean = false,
    val newServerName: TextFieldValue = TextFieldValue(""),
    val newServerDescription: TextFieldValue = TextFieldValue(""),
    val newServerOwner: Set<String> = emptySet(),
    val newServerOwnerSearchQuery: String = "",
    val newServerStartWhenInstalled: Boolean = true,
    val newServerNode: Set<String> = emptySet(),
    val newServerDefaultAllocation: Set<String> = emptySet(),
    val newServerAdditionalAllocations: Set<String> = emptySet(),
    val newServerDatabaseLimit: TextFieldValue = TextFieldValue("0"),
    val newServerAllocationLimit: TextFieldValue = TextFieldValue("0"),
    val newServerBackupLimit: TextFieldValue = TextFieldValue("0"),
    val newServerCpuLimit: TextFieldValue = TextFieldValue("0"),
    val newServerCpuPinning: TextFieldValue = TextFieldValue(""),
    val newServerMemory: TextFieldValue = TextFieldValue(""),
    val newServerSwap: TextFieldValue = TextFieldValue("0"),
    val newServerDisk: TextFieldValue = TextFieldValue(""),
    val newServerIo: TextFieldValue = TextFieldValue("500"),
    val newServerEnableOOMKiller: Boolean = false,
    val newServerNest: Set<String> = emptySet(),
    val newServerEgg: Set<String> = emptySet(),
    val newServerSkipEggInstallScript: Boolean = false,
    val newServerDockerImage: Set<String> = emptySet(),
    val newServerCustomDockerImage: TextFieldValue = TextFieldValue(""),
    val newServerStartupCommand: TextFieldValue = TextFieldValue(""),
    val newServerVariables: List<ApplicationServerVariable> = emptyList(),
    val newServerVariableContent: Map<String, TextFieldValue> = emptyMap()
)

private const val TAG = "ApplicationServersViewModel"

class ApplicationServersViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServersUiState> = MutableStateFlow(ApplicationServersUiState())
    val state: StateFlow<ApplicationServersUiState> = _state.asStateFlow()

    private var ownerSearchJob: kotlinx.coroutines.Job? = null

    fun updateData(
        context: Context,
        filterUuid: String? = null,
        filterUuidShort: String? = null,
        filterName: String? = null,
        filterImage: String? = null,
        filterExternalId: String? = null,
        sort: ListServersQuerySort? = null,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    nodesLoading = true,
                    nestsLoading = true,
                    servers = null,
                    isRefreshing = isRefresh
                )
            }

            val serversRes = listServers(
                context = context,
                filterUuid = filterUuid,
                filterUuidShort = filterUuidShort,
                filterName = filterName,
                filterImage = filterImage,
                filterExternalId = filterExternalId,
                sort = sort,
                page = _state.value.page,
                include = listOf(
                    ListServersQueryInclude.USER,
                    ListServersQueryInclude.NODE,
                    ListServersQueryInclude.ALLOCATIONS
                ).toQueryString()
            )

            val servers = serversRes.getOrNull()

            _state.update {
                it.copy(
                    servers = servers?.data ?: emptyList(),
                    pagination = servers?.meta?.pagination,
                    isLoading = false,
                    isRefreshing = false
                )
            }

            val nodes = listNodes(context)

            _state.update {
                it.copy(
                    nodes = nodes,
                    nodesLoading = false
                )
            }

            val firstNode = nodes.firstOrNull()

            setNewServerNode(
                firstNode?.attributes?.id?.toString()
                    ?.let { setOf(it) }
                    ?: emptySet()
            )

            val nests = listNests(context)

            _state.update {
                it.copy(
                    nests = nests,
                    nestsLoading = false,
                )
            }

            val firstNest = nests.firstOrNull()

            setNewServerNest(
                firstNest?.attributes?.id?.toString()
                    ?.let { setOf(it) }
                    ?: emptySet()
            )
        }
    }

    fun updateOwnerQuery(
        context: Context
    ) {
        ownerSearchJob?.cancel()

        _state.update {
            it.copy(
                newServerOwnerSuggestionsLoading = false
            )
        }

        val currentOwner = _state.value.newServerOwnerSuggestions.find { it.attributes.id.toString() in _state.value.newServerOwner }

        val query = _state.value.newServerOwnerSearchQuery

        if (query.length < 2 && _state.value.newServerOwner.isEmpty()) {
            _state.update {
                it.copy(
                    newServerOwnerSuggestions = listOfNotNull(currentOwner)
                )
            }

            return
        }

        ownerSearchJob = viewModelScope.launch {
            delay(300.milliseconds)

            _state.update {
                it.copy(
                    newServerOwnerSuggestionsLoading = true
                )
            }

            val usersRes = listUsers(
                context = context,
                filterEmail = query,
            )

            val users = usersRes.getOrNull()

            if (usersRes.isFailure || users == null) {
                _state.update {
                    it.copy(
                        newServerOwnerSuggestionsLoading = false
                    )
                }

                return@launch
            }

            val usersList = if (currentOwner != null) {
                if (currentOwner.attributes.id in users.data.map { it.attributes.id }) {
                    users.data
                } else {
                    users.data + currentOwner
                }
            } else {
                users.data
            }

            _state.update {
                it.copy(
                    newServerOwnerSuggestions = usersList,
                    newServerOwnerSuggestionsLoading = false
                )
            }
        }
    }

    private suspend fun listNodes(
        context: Context
    ): List<ApplicationNode> {
        val outputNodes = mutableListOf<ApplicationNode>()

        var currentPage = 1L
        var hasNextPage = true

        while (hasNextPage) {
            val nodesRes = listNodes(
                context = context,
                page = currentPage,
                include = listOf(
                    ListNodesQueryInclude.ALLOCATIONS,
                    ListNodesQueryInclude.LOCATION
                ).toQueryString()
            )

            if (nodesRes.isFailure) break

            val nodes = nodesRes.getOrNull() ?: break

            outputNodes.addAll(nodes.data)

            val nextLink = nodes.meta.pagination.links.next

            if (!nextLink.isNullOrEmpty()) {
                currentPage++
            } else {
                hasNextPage = false
            }
        }

        return outputNodes
    }

    private suspend fun listNests(
        context: Context
    ): List<ApplicationNest> {
        val outputNests = mutableListOf<ApplicationNest>()

        var currentPage = 1L
        var hasNextPage = true

        while (hasNextPage) {
            val nestsRes = listNests(
                context = context,
                page = currentPage,
                include = listOf(
                    ListNestsQueryInclude.EGGS,
                    ListNestsQueryInclude.EGGS + ListNestEggsQueryInclude.VARIABLES
                ).toQueryString()
            )

            if (nestsRes.isFailure) break

            val nests = nestsRes.getOrNull() ?: break

            outputNests.addAll(nests.data)

            val nextLink = nests.meta.pagination.links.next

            if (!nextLink.isNullOrEmpty()) {
                currentPage++
            } else {
                hasNextPage = false
            }
        }

        return outputNests
    }

    fun setPage(page: Long) {
        if (page == _state.value.page) return

        _state.update {
            it.copy(
                servers = null,
                page = page
            )
        }
    }

    fun showCreateServerPopup() {
        _state.update {
            it.copy(
                showCreateServerPopup = true
            )
        }
    }

    fun hideCreateServerPopup(
        skipLoading: Boolean = false
    ) {
        if (_state.value.isLoading && !skipLoading) return

        val firstNode = _state.value.nodes.firstOrNull()
        val firstNest = _state.value.nests.firstOrNull()
        val firstEgg = firstNest?.attributes?.relationships?.eggs?.data?.firstOrNull()

        _state.update {
            it.copy(
                showCreateServerPopup = false,
                newServerOwner = emptySet(),
                newServerOwnerSearchQuery = ""
            )
        }

        setNewServerName(TextFieldValue(""))
        setNewServerDescription(TextFieldValue(""))
        setNewServerNode(
            firstNode?.attributes?.id?.toString()
                ?.let { setOf(it) }
                ?: emptySet()
        )
        setNewServerDatabaseLimit(TextFieldValue("0"))
        setNewServerAllocationLimit(TextFieldValue("0"))
        setNewServerBackupLimit(TextFieldValue("0"))
        setNewServerCpuLimit(TextFieldValue("0"))
        setNewServerCpuPinning(TextFieldValue(""))
        setNewServerMemory(TextFieldValue(""))
        setNewServerSwap(TextFieldValue("0"))
        setNewServerDisk(TextFieldValue(""))
        setNewServerIo(TextFieldValue("500"))
        setNewServerEnableOOMKiller(false)
        setNewServerNest(
            firstNest?.attributes?.id?.toString()
                ?.let { setOf(it) } ?: emptySet()
        )
        setNewServerEgg(
            firstEgg?.attributes?.id?.toString()
                ?.let { setOf(it) }
                ?: emptySet()
        )
        setNewServerSkipEggInstallScript(false)
    }

    fun setNewServerName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newServerName = name
            )
        }
    }

    fun setNewServerDescription(description: TextFieldValue) {
        _state.update {
            it.copy(
                newServerDescription = description
            )
        }
    }

    fun setNewServerOwner(
        context: Context,
        owner: Set<String>
    ) {
        _state.update {
            it.copy(
                newServerOwner = owner
            )
        }

        updateOwnerQuery(context)
    }

    fun setNewServerOwnerSearchQuery(
        context: Context,
        query: String
    ) {
        _state.update {
            it.copy(
                newServerOwnerSearchQuery = query
            )
        }

        updateOwnerQuery(context)
    }

    fun setNewServerStartWhenInstalled(startWhenInstalled: Boolean) {
        _state.update {
            it.copy(
                newServerStartWhenInstalled = startWhenInstalled
            )
        }
    }

    fun setNewServerNode(selectedNode: Set<String>) {
        _state.update {
            it.copy(
                newServerNode = selectedNode
            )
        }

        val nodeId = selectedNode.firstOrNull()?.toLongOrNull()

        val node = _state.value.nodes.find { it.attributes.id == nodeId }
        val allocations = node?.attributes?.relationships?.allocations?.data
        val availableAllocations = allocations?.filter { !it.attributes.assigned }
        val firstAllocation = availableAllocations?.firstOrNull()?.attributes?.id?.toString()

        setNewServerAdditionalAllocations(emptySet())

        if (firstAllocation == null) {
            setNewServerDefaultAllocation(emptySet())

            return
        }

        setNewServerDefaultAllocation(setOf(firstAllocation))
    }

    fun setNewServerDefaultAllocation(allocation: Set<String>) {
        _state.update {
            it.copy(
                newServerDefaultAllocation = allocation
            )
        }
    }

    fun setNewServerAdditionalAllocations(allocations: Set<String>) {
        _state.update {
            it.copy(
                newServerAdditionalAllocations = allocations
            )
        }
    }

    fun setNewServerDatabaseLimit(limit: TextFieldValue) {
        _state.update {
            it.copy(
                newServerDatabaseLimit = limit
            )
        }
    }

    fun setNewServerAllocationLimit(limit: TextFieldValue) {
        _state.update {
            it.copy(
                newServerAllocationLimit = limit
            )
        }
    }

    fun setNewServerBackupLimit(limit: TextFieldValue) {
        _state.update {
            it.copy(
                newServerBackupLimit = limit
            )
        }
    }

    fun setNewServerCpuLimit(limit: TextFieldValue) {
        _state.update {
            it.copy(
                newServerCpuLimit = limit
            )
        }
    }

    fun setNewServerCpuPinning(pinning: TextFieldValue) {
        _state.update {
            it.copy(
                newServerCpuPinning = pinning
            )
        }
    }

    fun setNewServerMemory(memory: TextFieldValue) {
        _state.update {
            it.copy(
                newServerMemory = memory
            )
        }
    }

    fun setNewServerSwap(swap: TextFieldValue) {
        _state.update {
            it.copy(
                newServerSwap = swap
            )
        }
    }

    fun setNewServerDisk(disk: TextFieldValue) {
        _state.update {
            it.copy(
                newServerDisk = disk
            )
        }
    }

    fun setNewServerIo(io: TextFieldValue) {
        _state.update {
            it.copy(
                newServerIo = io
            )
        }
    }

    fun setNewServerEnableOOMKiller(enableOOMKiller: Boolean) {
        _state.update {
            it.copy(
                newServerEnableOOMKiller = enableOOMKiller
            )
        }
    }

    fun setNewServerNest(selectedNest: Set<String>) {
        _state.update {
            it.copy(
                newServerNest = selectedNest
            )
        }

        val nestId = selectedNest.firstOrNull()?.toLongOrNull()
        val nest = _state.value.nests.find { it.attributes.id == nestId }

        if (nest == null) {
            setNewServerEgg(emptySet())

            return
        }

        val eggs = nest.attributes.relationships?.eggs?.data
        val firstEgg = eggs?.firstOrNull()?.attributes?.id?.toString()

        if (firstEgg == null) {
            setNewServerEgg(emptySet())

            return
        }

        setNewServerEgg(setOf(firstEgg))
    }

    fun setNewServerEgg(selectedEgg: Set<String>) {
        _state.update {
            it.copy(
                newServerEgg = selectedEgg
            )
        }

        val nestId = _state.value.newServerNest.firstOrNull()?.toLongOrNull()
        val eggId = selectedEgg.firstOrNull()?.toLongOrNull()

        val nest = _state.value.nests.find { it.attributes.id == nestId }
        val eggs = nest?.attributes?.relationships?.eggs?.data
        val egg = eggs?.find { it.attributes.id == eggId }

        if (egg == null) {
            setNewServerDockerImage(emptySet())
            setNewServerStartupCommand(TextFieldValue("ERROR: Startup Not Defined!"))
            setNewServerVariables(emptyList())
            setNewServerVariableContents(emptyMap())

            return
        }

        val dockerImage = egg.attributes.dockerImage
        val startupCommand = egg.attributes.startup
        val variables = egg.attributes.relationships?.variables?.data ?: emptyList()

        setNewServerDockerImage(setOf(dockerImage))
        setNewServerStartupCommand(TextFieldValue(startupCommand))
        setNewServerVariables(variables)
        setNewServerVariableContents(
            variables.associate {
                it.attributes.envVariable to TextFieldValue(it.attributes.defaultValue)
            }
        )
    }

    fun setNewServerSkipEggInstallScript(skipEggInstallScript: Boolean) {
        _state.update {
            it.copy(
                newServerSkipEggInstallScript = skipEggInstallScript
            )
        }
    }

    fun setNewServerDockerImage(dockerImage: Set<String>) {
        _state.update {
            it.copy(
                newServerDockerImage = dockerImage
            )
        }
    }

    fun setNewServerCustomDockerImage(customDockerImage: TextFieldValue) {
        _state.update {
            it.copy(
                newServerCustomDockerImage = customDockerImage
            )
        }
    }

    fun setNewServerStartupCommand(startupCommand: TextFieldValue) {
        _state.update {
            it.copy(
                newServerStartupCommand = startupCommand
            )
        }
    }

    private fun setNewServerVariableContents(variables: Map<String, TextFieldValue>) {
        _state.update {
            it.copy(
                newServerVariableContent = variables
            )
        }
    }

    fun setNewServerVariableContent(variable: String, content: TextFieldValue) {
        _state.update {
            it.copy(
                newServerVariableContent = it.newServerVariableContent
                    .toMutableMap()
                    .apply {
                        put(variable, content)
                    }
                    .toMap()
            )
        }
    }

    fun setNewServerVariables(variables: List<ApplicationServerVariable>) {
        _state.update {
            it.copy(
                newServerVariables = variables
            )
        }
    }

    fun createServer(
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

            val createServerRes = createServer(
                context = context,
                dockerImage = _state.value.newServerCustomDockerImage.text.trim().takeIf { it.isNotBlank() } ?: _state.value.newServerDockerImage.firstOrNull() ?: "",
                egg = _state.value.newServerEgg.firstOrNull()?.toLongOrNull() ?: 1L,
                environment = _state.value.newServerVariableContent.mapValues { it.value.text.trim() },
                featureLimits = CreateServerBody.FeatureLimits(
                    databases = _state.value.newServerDatabaseLimit.text.trim().toLongOrNull() ?: 0L,
                    allocations = _state.value.newServerAllocationLimit.text.trim().toLongOrNull() ?: 0L,
                    backups = _state.value.newServerBackupLimit.text.trim().toLongOrNull() ?: 0L
                ),
                limits = CreateServerBody.Limits(
                    memory = _state.value.newServerMemory.text.trim().toLongOrNull() ?: 0L,
                    swap = _state.value.newServerSwap.text.trim().toLongOrNull() ?: 0L,
                    disk = _state.value.newServerDisk.text.trim().toLongOrNull() ?: 0L,
                    io = _state.value.newServerIo.text.trim().toLongOrNull() ?: 500L,
                    cpu = _state.value.newServerCpuLimit.text.trim().toLongOrNull() ?: 0L,
                    threads = _state.value.newServerCpuPinning.text.trim().takeIf { it.isNotBlank() }
                ),
                allocation = CreateServerBody.Allocation(
                    default = _state.value.newServerDefaultAllocation.firstOrNull()?.toLongOrNull() ?: 1L,
                    additional = _state.value.newServerAdditionalAllocations.mapNotNull { it.toLongOrNull() }
                ),
                name = _state.value.newServerName.text.trim(),
                startup = _state.value.newServerStartupCommand.text.trim(),
                user = _state.value.newServerOwner.firstOrNull()?.toLongOrNull() ?: 1L,
                description = _state.value.newServerDescription.text.trim().takeIf { it.isNotBlank() },
                skipEggInstallScript = _state.value.newServerSkipEggInstallScript,
                oomDisabled = !_state.value.newServerEnableOOMKiller,
                startOnCompletion = _state.value.newServerStartWhenInstalled,
            )

            createServerRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    hideCreateServerPopup(true)

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to create server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create server: ${error.message}")
                }
        }
    }
}