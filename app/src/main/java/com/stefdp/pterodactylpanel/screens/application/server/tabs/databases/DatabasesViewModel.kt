package com.stefdp.pterodactylpanel.screens.application.server.tabs.databases

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServerDatabase
import com.stefdp.pterodactylpanel.network.application.requests.deleteServerDatabase
import com.stefdp.pterodactylpanel.network.application.requests.resetServerDatabasePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationServerDatabasesTabUiState(
    val isLoading: Boolean = false,
    val databases: List<ApplicationServerDatabase> = emptyList(),
    val databaseToDelete: Long? = null,
)

private const val TAG = "ApplicationServerDatabasesTabViewModel"

class ApplicationServerDatabasesTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerDatabasesTabUiState> = MutableStateFlow(ApplicationServerDatabasesTabUiState())
    val state: StateFlow<ApplicationServerDatabasesTabUiState> = _state.asStateFlow()

    private var serverId: Long? = null

    fun init(server: ApplicationServer?) {
        this.serverId = server?.attributes?.id

        _state.update {
            it.copy(
                isLoading = false,
                databases = server?.attributes?.relationships?.databases?.data ?: emptyList()
            )
        }
    }

    fun setDatabaseToDelete(
        databaseId: Long?,
        skipLoading: Boolean = false
    ) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                databaseToDelete = databaseId
            )
        }
    }

    fun deleteDatabase(
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

            val databaseId = _state.value.databaseToDelete

            if (databaseId == null) {
                onError("Missing Database ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteDatabaseRes = deleteServerDatabase(
                context = context,
                serverId = serverId!!,
                databaseId = databaseId
            )

            deleteDatabaseRes
                .onSuccess {
                    reload(
                        false,
                        {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    databaseToDelete = null
                                )
                            }

                            onSuccess()
                        },
                        true,
                        { error ->
                            Logger.error(TAG, "Failed to refresh server databases after deletion: $error")

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    databaseToDelete = null
                                )
                            }

                            onError("Failed to refresh server databases after deletion: $error")
                        }
                    )
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to delete server database: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }

                    onError("Failed to delete server database: ${error.message}")
                }
        }
    }

    fun resetDatabasePassword(
        databaseId: Long,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
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

            val resetPasswordRes = resetServerDatabasePassword(
                context = context,
                serverId = serverId!!,
                databaseId = databaseId
            )

            resetPasswordRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to reset server database password: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to reset server database password: ${error.message}")
                }
        }
    }
}