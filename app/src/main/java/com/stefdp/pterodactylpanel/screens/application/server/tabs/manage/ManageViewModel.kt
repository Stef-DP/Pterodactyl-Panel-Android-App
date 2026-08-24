package com.stefdp.pterodactylpanel.screens.application.server.tabs.manage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationAllocation
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.listNodes
import com.stefdp.pterodactylpanel.network.application.requests.reinstallServer
import com.stefdp.pterodactylpanel.network.application.requests.suspendServer
import com.stefdp.pterodactylpanel.network.application.requests.unsuspendServer
import com.stefdp.pterodactylpanel.network.models.toQueryString
import com.stefdp.pterodactylpanel.screens.application.server.installingStatuses
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationServerManageTabUiState(
    val isLoading: Boolean = false,
    val isInstalling: Boolean = false,
    val isSuspended: Boolean = false
)

private const val TAG = "ApplicationServerManageTabViewModel"

class ApplicationServerManageTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerManageTabUiState> = MutableStateFlow(ApplicationServerManageTabUiState())
    val state: StateFlow<ApplicationServerManageTabUiState> = _state.asStateFlow()

    private var serverId: Long? = null

    fun init(server: ApplicationServer?) {
        this.serverId = server?.attributes?.id

        _state.update {
            it.copy(
                isInstalling = server?.attributes?.status in installingStatuses,
                isSuspended = server?.attributes?.suspended == true || server?.attributes?.status == ApplicationServer.Attributes.Status.SUSPENDED,
            )
        }
    }

    fun reinstallServer(
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
            if (serverId == null) {
                onError("Missing Server ID")

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
                    reload(
                        false,
                        {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }

                            onSuccess()
                        },
                        true,
                        { _ ->
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to reinstall server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to reinstall server: ${error.message}")
                }
        }
    }

    fun suspendServer(
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
            if (serverId == null) {
                onError("Missing Server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val suspendServerRes = suspendServer(
                context = context,
                serverId = serverId!!,
            )

            suspendServerRes
                .onSuccess {
                    reload(
                        false,
                        {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }

                            onSuccess()
                        },
                        true,
                        { _ ->
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to suspend server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to suspend server: ${error.message}")
                }
        }
    }

    fun unsuspendServer(
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
            if (serverId == null) {
                onError("Missing Server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val unsuspendServerRes = unsuspendServer(
                context = context,
                serverId = serverId!!,
            )

            unsuspendServerRes
                .onSuccess {
                    reload(
                        false,
                        {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }

                            onSuccess()
                        },
                        true,
                        { _ ->
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to unsuspend server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to unsuspend server: ${error.message}")
                }
        }
    }
}