package com.stefdp.pterodactylpanel.screens.application.node.tabs.about

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.responses.GetNodeConfigurationResponse
import com.stefdp.pterodactylpanel.network.application.requests.deleteNode
import com.stefdp.pterodactylpanel.network.node.models.responses.GetNodeSystemV2Response
import com.stefdp.pterodactylpanel.network.node.requests.getNodeStatus
import com.stefdp.pterodactylpanel.network.node.requests.getNodeSystemV2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNodeAboutTabUiState(
    val isLoading: Boolean = false,
    val systemData: GetNodeSystemV2Response? = null,
    val node: ApplicationNode? = null,
)

private const val TAG = "ApplicationNodeAboutTabViewModel"

class ApplicationNodeAboutTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNodeAboutTabUiState> = MutableStateFlow(ApplicationNodeAboutTabUiState())
    val state: StateFlow<ApplicationNodeAboutTabUiState> = _state.asStateFlow()

    private var nodeId: Long? = null

    fun init(
        node: ApplicationNode?,
        nodeConfiguration: GetNodeConfigurationResponse?,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            this@ApplicationNodeAboutTabViewModel.nodeId = node?.attributes?.id

            _state.update {
                it.copy(
                    isLoading = true,
                    node = node
                )
            }

            val nodeScheme = if (node?.attributes?.scheme == ApplicationNode.Attributes.Scheme.HTTPS)
                "https"
            else
                "http"

            val nodeFQDN = node?.attributes?.fqdn
            val nodePort = node?.attributes?.daemonListen

            val nodeBaseUrl = "$nodeScheme://$nodeFQDN:$nodePort"

            val nodeSystemRes = getNodeSystemV2(
                nodeUrl = nodeBaseUrl,
                token = nodeConfiguration?.token ?: ""
            )

            nodeSystemRes
                .onSuccess { systemData ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            systemData = systemData
                        )
                    }
                }
                .onFailure { error ->
                    val nodeStatusRes = getNodeStatus(nodeBaseUrl)

                    if (nodeStatusRes.isSuccess) {
                        Logger.error(TAG, "Failed to fetch node data: ${error.message}")

                        onError("Failed to fetch node data: ${error.message}")
                    } else {
                        onError("Failed to fetch node data: Node is offline")
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun deleteNode(
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

            val deleteNodeRes = deleteNode(
                context = context,
                nodeId = nodeId!!
            )

            deleteNodeRes
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to delete node: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete node: ${error.message}")
                }
        }
    }
}