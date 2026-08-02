package com.stefdp.pterodactylpanel.screens.client.server.tabs.users

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.createServerSubuser
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerSubuser
import com.stefdp.pterodactylpanel.network.client.requests.listServerSubusers
import com.stefdp.pterodactylpanel.network.client.requests.updateServerSubuser
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
    val newSubuserPermissions: Map<ServerSubuser.Permissions, Boolean> = emptyMap()
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

    fun hideCreateNewUserPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateNewUserPopup = false,
                newSubuserPermissions = ServerSubuser.Permissions.entries.associateWith { false }
            )
        }
    }

    fun createUser(
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

            val createUserRes = createServerSubuser(
                context = context,
                serverId = serverId!!,
                email = _state.value.newUserEmail.text.trim(),
                permissions = _state.value.newSubuserPermissions.filter { it.value }.keys.toList()
            )

            createUserRes
                .onSuccess {
                    updateUsers(
                        context = context,
                        onSuccess = {
                            hideCreateNewUserPopup(skipLoading = true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerUsersTabViewModel", "Failed to create server subuser: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create server subuser: ${error.message}")
                }
        }
    }

    fun setUserToEdit(
        subuser: ServerSubuser?,
        skipLoading: Boolean = false
    ) {
        if (subuser == null && _state.value.isLoading && !skipLoading) return

        if (subuser == null) {
            resetNewSubuserPermissions()

            _state.update {
                it.copy(
                    userToEdit = null,
                    newSubuserPermissions = ServerSubuser.Permissions.entries.associateWith { false }
                )
            }
        } else {
            _state.update {
                it.copy(
                    userToEdit = subuser.attributes.uuid,
                    newSubuserPermissions = ServerSubuser.Permissions.entries.associateWith { permission -> subuser.attributes.permissions.contains(permission) }
                )
            }
        }
    }

    fun updateUser(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val userUuid = _state.value.userToEdit

            if (userUuid == null) {
                onError("Missing user UUID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateUserRes = updateServerSubuser(
                context = context,
                serverId = serverId!!,
                userUuid = userUuid,
                permissions = _state.value.newSubuserPermissions.filter { it.value }.keys.toList()
            )

            updateUserRes
                .onSuccess {
                    updateUsers(
                        context = context,
                        onSuccess = {
                            setUserToEdit(null, skipLoading = true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerUsersTabViewModel", "Failed to update server subuser: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server subuser: ${error.message}")
                }
        }
    }

    fun setUserToDelete(
        subuser: ServerSubuser?,
        skipLoading: Boolean = false
    ) {
        if (subuser == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                userToDelete = subuser?.attributes?.uuid
            )
        }
    }

    fun deleteUser(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val userUuid = _state.value.userToDelete

            if (userUuid == null) {
                onError("Missing user UUID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteUserRes = deleteServerSubuser(
                context = context,
                serverId = serverId!!,
                userUuid = userUuid
            )

            deleteUserRes
                .onSuccess {
                    updateUsers(
                        context = context,
                        onSuccess = {
                            setUserToDelete(null, skipLoading = true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerUsersTabViewModel", "Failed to delete server subuser: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete server subuser: ${error.message}")
                }
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
                newSubuserPermissions = ServerSubuser.Permissions.entries.associateWith { permission ->
                    permission == ServerSubuser.Permissions.WEBSOCKET_CONNECT
                }
            )
        }
    }
}