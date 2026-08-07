package com.stefdp.pterodactylpanel.screens.client.server.tabs.databases

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerDatabase
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.requests.ListServerDatabasesQueryInclude
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.createServerDatabase
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerDatabase
import com.stefdp.pterodactylpanel.network.client.requests.listServerDatabases
import com.stefdp.pterodactylpanel.network.client.requests.rotateServerDatabasePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerDatabasesTabUiState(
    val isLoading: Boolean = false,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val databases: List<ServerDatabase> = emptyList(),
    val databaseToDelete: String? = null,
    val databaseToShowDetails: String? = null,
    val confirmDatabaseNameValue: TextFieldValue = TextFieldValue(""),
    val showCreateDatabasePopup: Boolean = false,
    val newDatabaseName: TextFieldValue = TextFieldValue(""),
    val newDatabaseAllowedIp: TextFieldValue = TextFieldValue(""),
)

private const val TAG = "ClientServerDatabasesTabViewModel"

class ClientServerDatabasesTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerDatabasesTabUiState> = MutableStateFlow(ClientServerDatabasesTabUiState())
    val state: StateFlow<ClientServerDatabasesTabUiState> = _state.asStateFlow()

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

    fun updateDatabases(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
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

            val serverDatabasesRes = listServerDatabases(
                context = context,
                serverId = serverId!!,
                include = ListServerDatabasesQueryInclude.PASSWORD.toString()
            )

            serverDatabasesRes
                .onSuccess { databases ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            databases = databases.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch server databases: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server databases: ${error.message}")
                }
        }
    }

    fun setDatabaseToDelete(
        database: String?,
        skipLoading: Boolean = false
    ) {
        if (database == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                databaseToDelete = database,
                confirmDatabaseNameValue = TextFieldValue("")
            )
        }
    }

    fun deleteDatabase(
        context: Context,
        databaseId: String,
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

            val deleteDatabaseRes = deleteServerDatabase(
                context = context,
                serverId = serverId!!,
                databaseId = databaseId
            )

            deleteDatabaseRes
                .onSuccess {
                    setDatabaseToDelete(null, true)

                    updateDatabases(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to delete database: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete database: ${error.message}")
                }
        }
    }

    fun setDatabaseToShowDetails(database: String?) {
        _state.update {
            it.copy(
                databaseToShowDetails = database
            )
        }
    }

    fun rotateDatabasePassword(
        context: Context,
        databaseId: String,
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

            val rotatePasswordRes = rotateServerDatabasePassword(
                context = context,
                serverId = serverId!!,
                databaseId = databaseId
            )

            rotatePasswordRes
                .onSuccess { database ->
                    val databaseIndex = _state.value.databases.indexOfFirst { it.attributes.id == database.attributes.id }

                    if (databaseIndex == -1) {
                        updateDatabases(
                            context = context,
                            onError = onError,
                            onSuccess = onSuccess
                        )
                    } else {
                        val updatedDatabases = _state.value.databases.toMutableList()
                        updatedDatabases[databaseIndex] = database

                        _state.update {
                            it.copy(
                                isLoading = false,
                                databases = updatedDatabases.toList()
                            )
                        }

                        onSuccess()
                    }
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to rotate database password: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to rotate database password: ${error.message}")
                }
        }
    }

    fun setConfirmDeleteDatabaseNameValue(name: TextFieldValue) {
        _state.update {
            it.copy(
                confirmDatabaseNameValue = name
            )
        }
    }

    fun showCreateDatabasePopup() {
        _state.update {
            it.copy(
                showCreateDatabasePopup = true,
            )
        }
    }

    fun hideCreateDatabasePopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateDatabasePopup = false,
                newDatabaseName = TextFieldValue(""),
                newDatabaseAllowedIp = TextFieldValue(""),
            )
        }
    }

    fun createDatabase(
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

            val allowedIp = if (_state.value.newDatabaseAllowedIp.text.trim().isBlank()) {
                "%"
            } else {
                _state.value.newDatabaseAllowedIp.text.trim()
            }

            val createDatabaseRes = createServerDatabase(
                context = context,
                serverId = serverId!!,
                databaseName = _state.value.newDatabaseName.text.trim(),
                allowedIp = allowedIp
            )

            createDatabaseRes
                .onSuccess {
                    hideCreateDatabasePopup(true)

                    updateDatabases(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to create database: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create database: ${error.message}")
                }
        }
    }

    fun setNewDatabaseName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newDatabaseName = name
            )
        }
    }

    fun setNewDatabaseAllowedIp(ip: TextFieldValue) {
        _state.update {
            it.copy(
                newDatabaseAllowedIp = ip
            )
        }
    }
}