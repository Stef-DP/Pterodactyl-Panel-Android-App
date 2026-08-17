package com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationAllocation
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodeAllocationsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNodeAllocationsResponse
import com.stefdp.pterodactylpanel.network.application.requests.createNodeAllocation
import com.stefdp.pterodactylpanel.network.application.requests.deleteNodeAllocation
import com.stefdp.pterodactylpanel.network.application.requests.listNodeAllocations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNodeAllocationsTabUiState(
    val isLoading: Boolean = true,
    val allocations: List<ApplicationAllocation> = emptyList(),
    val page: Long = 1L,
    val pagination: ListNodeAllocationsResponse.Meta.Pagination? = null,
    val selectedAllocations: Set<Long> = emptySet(),
    val allocationToDelete: Long? = null,
    val showBulkDeletePopup: Boolean = false,
    val showCreateAllocationsPopup: Boolean = false,
    val newAllocationsIp: TextFieldValue = TextFieldValue(""),
    val newAllocationsIpAlias: TextFieldValue = TextFieldValue(""),
    val newAllocationsPorts: TextFieldValue = TextFieldValue(""),
)

private const val TAG = "ApplicationNodeAllocationsTabViewModel"

class ApplicationNodeAllocationsTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNodeAllocationsTabUiState> = MutableStateFlow(ApplicationNodeAllocationsTabUiState())
    val state: StateFlow<ApplicationNodeAllocationsTabUiState> = _state.asStateFlow()

    private var nodeId: Long? = null

    fun init(node: ApplicationNode?) {
        nodeId = node?.attributes?.id
    }

    fun updateAllocations(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (nodeId == null) {
                onError("Missing Node ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true,
                )
            }

            val allocationsRes = listNodeAllocations(
                context = context,
                nodeId = nodeId!!,
                page = _state.value.page,
                include = ListNodeAllocationsQueryInclude.toQueryString(
                    ListNodeAllocationsQueryInclude.SERVER
                )
            )

            allocationsRes
                .onSuccess { allocations ->
                    _state.update {
                        it.copy(
                            allocations = allocations.data,
                            pagination = allocations.meta.pagination,
                            isLoading = false,
                            selectedAllocations = emptySet()
                        )
                    }

                    onSuccess()
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

    fun setSelectedAllocations(selectedAllocations: Set<Long>) {
        _state.update {
            it.copy(
                selectedAllocations = selectedAllocations
            )
        }
    }

    fun setPage(page: Long) {
        if (page == _state.value.page) return

        _state.update {
            it.copy(
                page = page
            )
        }
    }

    fun setAllocationToDelete(
        allocationId: Long?,
        skipLoading: Boolean = false
    ) {
        if (allocationId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                allocationToDelete = allocationId
            )
        }
    }

    private suspend fun deleteAllocationInternal(
        context: Context,
        allocationId: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        bulk: Boolean = false
    ) {
        if (nodeId == null) {
            onError("Missing Node ID")

            return
        }

        _state.update {
            it.copy(
                isLoading = true
            )
        }

        val deleteAllocationRes = deleteNodeAllocation(
            context = context,
            nodeId = nodeId!!,
            allocationId = allocationId
        )

        deleteAllocationRes
            .onSuccess {
                if (!bulk) {
                    updateAllocations(
                        context = context,
                        onSuccess = {
                            onSuccess()

                            setAllocationToDelete(null, true)
                        },
                        onError = onError
                    )
                } else {
                    onSuccess()
                }
            }
            .onFailure { error ->
                val errorMessage = if (bulk) {
                    val allocation = _state.value.allocations.find { it.attributes.id == allocationId }

                    "Failed to delete node allocation `${allocation?.attributes?.ip}:${allocation?.attributes?.port}` (${error.message})"
                } else {
                    "Failed to delete node allocation: ${error.message}"
                }

                Logger.error(TAG, errorMessage)

                if (!bulk) {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }

                onError("Failed to delete node allocation: ${error.message}")
            }
    }

    fun deleteAllocation(
        context: Context,
        allocationId: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            deleteAllocationInternal(
                context = context,
                allocationId = allocationId,
                onSuccess = onSuccess,
                onError = onError,
            )
        }
    }

    fun showBulkDeletePopup() {
        _state.update {
            it.copy(
                showBulkDeletePopup = true
            )
        }
    }

    fun hideBulkDeletePopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showBulkDeletePopup = false,
                selectedAllocations = emptySet()
            )
        }
    }

    fun bulkDeleteAllocations(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            for (allocationId in _state.value.selectedAllocations) {
                deleteAllocationInternal(
                    context = context,
                    allocationId = allocationId,
                    onSuccess = {},
                    onError = onError,
                    bulk = true
                )
            }

            hideBulkDeletePopup(true)

            updateAllocations(
                context = context,
                onSuccess = {
                    onSuccess()
                },
                onError = onError,
            )
        }
    }

    fun showCreateAllocationsPopup() {
        _state.update {
            it.copy(
                showCreateAllocationsPopup = true
            )
        }
    }

    fun hideCreateAllocationsPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateAllocationsPopup = false,
                newAllocationsIp = TextFieldValue(""),
                newAllocationsIpAlias = TextFieldValue(""),
                newAllocationsPorts = TextFieldValue("")
            )
        }
    }

    fun setNewAllocationsIp(ip: TextFieldValue) {
        _state.update {
            it.copy(
                newAllocationsIp = ip
            )
        }
    }

    fun setNewAllocationsIpAlias(ipAlias: TextFieldValue) {
        _state.update {
            it.copy(
                newAllocationsIpAlias = ipAlias
            )
        }
    }

    fun setNewAllocationsPorts(ports: TextFieldValue) {
        _state.update {
            it.copy(
                newAllocationsPorts = ports
            )
        }
    }

    fun createAllocations(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (nodeId == null) {
                onError("Missing Node ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val createAllocationsRes = createNodeAllocation(
                context = context,
                nodeId = nodeId!!,
                ip = _state.value.newAllocationsIp.text.trim(),
                alias = _state.value.newAllocationsIpAlias.text.trim().takeIf { it.isNotBlank() },
                ports = _state.value.newAllocationsPorts.text.trim().split(
                    ", ",
                    ",",
                    " "
                )
            )

            createAllocationsRes
                .onSuccess {
                    updateAllocations(
                        context = context,
                        onSuccess = {
                            onSuccess()

                            hideCreateAllocationsPopup(true)
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to assign node allocations: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to assign node allocations: ${error.message}")
                }
        }
    }
}