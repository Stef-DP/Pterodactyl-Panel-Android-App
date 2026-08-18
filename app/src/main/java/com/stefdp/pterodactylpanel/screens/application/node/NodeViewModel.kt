package com.stefdp.pterodactylpanel.screens.application.node

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServersQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.responses.GetNodeConfigurationResponse
import com.stefdp.pterodactylpanel.network.application.requests.getNode
import com.stefdp.pterodactylpanel.network.application.requests.getNodeConfiguration
import com.stefdp.pterodactylpanel.network.models.plus
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNodeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val node: ApplicationNode? = null,
    val nodeConfiguration: GetNodeConfigurationResponse? = null,
    val currentTab: NodeTab = NodeTab.ABOUT
)

private const val TAG = "ApplicationNodeViewModel"

class ApplicationNodeViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNodeUiState> = MutableStateFlow(ApplicationNodeUiState())
    val state: StateFlow<ApplicationNodeUiState> = _state.asStateFlow()

    private var nodeId: Long? = null

    fun init(
        context: Context,
        nodeId: Long,
        onError: (String) -> Unit,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            this@ApplicationNodeViewModel.nodeId = nodeId

            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh,
                )
            }

            val nodeRes = getNode(
                context = context,
                nodeId = nodeId,
                include = listOf(
                    ListNodesQueryInclude.LOCATION,
                    ListNodesQueryInclude.SERVERS,
                    ListNodesQueryInclude.SERVERS + ListServersQueryInclude.USER,
                    ListNodesQueryInclude.SERVERS + ListServersQueryInclude.NEST,
                    ListNodesQueryInclude.SERVERS + ListServersQueryInclude.EGG,
                    ListNodesQueryInclude.ALLOCATIONS
                ).toQueryString()
            )

            nodeRes
                .onSuccess { node ->
                    val nodeConfigurationRes = getNodeConfiguration(
                        context = context,
                        nodeId = nodeId
                    )

                    nodeConfigurationRes
                        .onSuccess { nodeConfiguration ->
                            _state.update {
                                it.copy(
                                    node = node,
                                    nodeConfiguration = nodeConfiguration,
                                    isRefreshing = false,
                                    isLoading = false
                                )
                            }
                        }
                        .onFailure { error ->
                            Logger.error(TAG, "Failed to fetch node configuration: ${error.message}")

                            _state.update {
                                it.copy(
                                    isRefreshing = false,
                                    isLoading = false
                                )
                            }

                            onError("Failed to fetch node configuration: ${error.message}")
                        }
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch node: ${error.message}")

                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch node: ${error.message}")
                }
        }
    }

    fun setCurrentTab(tab: NodeTab) {
        _state.update {
            it.copy(currentTab = tab)
        }
    }
}