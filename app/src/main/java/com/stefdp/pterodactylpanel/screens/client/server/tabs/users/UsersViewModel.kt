package com.stefdp.pterodactylpanel.screens.client.server.tabs.users

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.listServerSubusers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerUsersTabUiState(
    val isLoading: Boolean = true,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val subusers: List<ServerSubuser> = emptyList(),
    val showCreateNewUserPopup: Boolean = false,
    val userToEdit: String? = null,
    val userToDelete: String? = null,
    val newUserEmail: TextFieldValue = TextFieldValue(""),
    val newSubuserPermissions: Map<ServerSubuser.Permissions, Boolean> = ServerSubuser.Permissions.entries.associateWith { false }
)

class ClientServerUsersTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerUsersTabUiState> = MutableStateFlow(ClientServerUsersTabUiState())
    val state: StateFlow<ClientServerUsersTabUiState> = _state.asStateFlow()

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

    fun updateUsers(
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

            val listUsersRes = listServerSubusers(
                context = context,
                serverId = serverId!!,
            )

            listUsersRes
                .onSuccess { subusers ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            subusers = subusers.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerUsersTabViewModel", "Failed to fetch server users: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server users: ${error.message}")
                }
        }
    }

    fun showCreateNewUserPopup() {
        resetNewSubuserPermissions()

        _state.update {
            it.copy(
                showCreateNewUserPopup = true
            )
        }
    }

    fun hideCreateScheduleTaskPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateNewUserPopup = false
            )
        }
    }

    fun setUserToEdit(
        userId: String?,
        skipLoading: Boolean = false
    ) {
        if (userId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                userToEdit = userId
            )
        }
    }

    fun setUserToDelete(
        userId: String?,
        skipLoading: Boolean = false
    ) {
        if (userId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                userToDelete = userId
            )
        }
    }

    fun setNewUserEmail(email: TextFieldValue) {
        _state.update {
            it.copy(
                newUserEmail = email
            )
        }
    }

    fun setNewSubuserPermissions(permissions: Map<ServerSubuser.Permissions, Boolean>) {
        _state.update {
            it.copy(
                newSubuserPermissions = permissions
            )
        }
    }

    fun resetNewSubuserPermissions() {
        _state.update {
            it.copy(
                newSubuserPermissions = ServerSubuser.Permissions.entries.associateWith { false }
            )
        }
    }
}