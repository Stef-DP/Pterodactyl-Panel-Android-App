package com.stefdp.pterodactylpanel.screens.application.users

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.models.requests.GetUsersQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListUsersQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.responses.ListUsersResponse
import com.stefdp.pterodactylpanel.network.application.requests.createUser
import com.stefdp.pterodactylpanel.network.application.requests.listUsers
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.onSuccess

data class ApplicationUsersUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val users: List<ApplicationUser>? = null,
    val page: Long = 1L,
    val pagination: ListUsersResponse.Meta.Pagination? = null,
    val showCreateUserPopup: Boolean = false,
    val newUserEmail: TextFieldValue = TextFieldValue(""),
    val newUserUsername: TextFieldValue = TextFieldValue(""),
    val newUserFirstName: TextFieldValue = TextFieldValue(""),
    val newUserLastName: TextFieldValue = TextFieldValue(""),
    val newUserDefaultLanguage: Set<String> = setOf(Languages.ENGLISH.code),
    val newUserIsAdmin: Boolean = false,
    val newUserPassword: TextFieldValue = TextFieldValue("")
)

private const val TAG = "ApplicationUsersViewModel"

class ApplicationUsersViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationUsersUiState> = MutableStateFlow(ApplicationUsersUiState())
    val state: StateFlow<ApplicationUsersUiState> = _state.asStateFlow()

    fun updateData(
        context: Context,
        filterEmail: String? = null,
        filterUuid: String? = null,
        filterUsername: String? = null,
        filterExternalId: String? = null,
        sort: GetUsersQuerySort? = null,
        isRefresh: Boolean = false,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    users = null,
                    isRefreshing = isRefresh
                )
            }

            val usersRes = listUsers(
                context = context,
                filterEmail = filterEmail,
                filterUuid = filterUuid,
                filterUsername = filterUsername,
                filterExternalId = filterExternalId,
                sort = sort,
                page = _state.value.page,
                include = listOf(
                    ListUsersQueryInclude.SERVERS
                ).toQueryString()
            )

            val users = usersRes.getOrNull()

            _state.update {
                it.copy(
                    users = users?.data ?: emptyList(),
                    pagination = users?.meta?.pagination,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    fun setPage(page: Long) {
        if (page == _state.value.page) return

        _state.update {
            it.copy(
                users = null,
                page = page
            )
        }
    }

    fun showCreateUserPopup() {
        _state.update {
            it.copy(
                showCreateUserPopup = true
            )
        }
    }

    fun hideCreateUserPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateUserPopup = false,
                newUserEmail = TextFieldValue(""),
                newUserUsername = TextFieldValue(""),
                newUserFirstName = TextFieldValue(""),
                newUserLastName = TextFieldValue(""),
                newUserDefaultLanguage = setOf(Languages.ENGLISH.code),
                newUserIsAdmin = false,
                newUserPassword = TextFieldValue("")
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

    fun setNewUserUsername(username: TextFieldValue) {
        _state.update {
            it.copy(
                newUserUsername = username
            )
        }
    }

    fun setNewUserFirstName(firstName: TextFieldValue) {
        _state.update {
            it.copy(
                newUserFirstName = firstName
            )
        }
    }

    fun setNewUserLastName(lastName: TextFieldValue) {
        _state.update {
            it.copy(
                newUserLastName = lastName
            )
        }
    }

    fun setNewUserDefaultLanguage(language: Set<String>) {
        _state.update {
            it.copy(
                newUserDefaultLanguage = language
            )
        }
    }

    fun setNewUserIsAdmin(isAdmin: Boolean) {
        _state.update {
            it.copy(
                newUserIsAdmin = isAdmin
            )
        }
    }

    fun setNewUserPassword(password: TextFieldValue) {
        _state.update {
            it.copy(
                newUserPassword = password
            )
        }
    }

    fun createUser(
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

            val createUserRes = createUser(
                context = context,
                email = _state.value.newUserEmail.text.trim(),
                username = _state.value.newUserUsername.text.trim(),
                firstName = _state.value.newUserFirstName.text.trim(),
                lastName = _state.value.newUserLastName.text.trim(),
                password = _state.value.newUserPassword.text.trim().takeIf { it.isNotBlank() },
                language = _state.value.newUserDefaultLanguage.firstOrNull(),
                rootAdmin = _state.value.newUserIsAdmin,
            )

            createUserRes
                .onSuccess {
                    updateData(
                        context = context
                    )

                    hideCreateUserPopup(true)

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to create user: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create user: ${error.message}")
                }
        }
    }
}