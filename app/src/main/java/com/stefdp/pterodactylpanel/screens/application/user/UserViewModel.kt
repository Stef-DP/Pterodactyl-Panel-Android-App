package com.stefdp.pterodactylpanel.screens.application.user

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.models.requests.ListUsersQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.deleteUser
import com.stefdp.pterodactylpanel.network.application.requests.getUser
import com.stefdp.pterodactylpanel.network.application.requests.updateUser
import com.stefdp.pterodactylpanel.network.models.toQueryString
import com.stefdp.pterodactylpanel.screens.application.users.Languages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.onSuccess

data class ApplicationUserUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val user: ApplicationUser? = null,
    val newUserEmail: TextFieldValue = TextFieldValue(""),
    val userUsername: TextFieldValue = TextFieldValue(""),
    val userFirstName: TextFieldValue = TextFieldValue(""),
    val userLastName: TextFieldValue = TextFieldValue(""),
    val userDefaultLanguage: Set<String> = setOf(Languages.ENGLISH.code),
    val userIsAdmin: Boolean = false,
    val userPassword: TextFieldValue = TextFieldValue(""),
    val ownsServers: Boolean = false
)

private const val TAG = "ApplicationUserViewModel"

class ApplicationUserViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationUserUiState> = MutableStateFlow(ApplicationUserUiState())
    val state: StateFlow<ApplicationUserUiState> = _state.asStateFlow()

    private var userId: Long? = null

    fun init(
        context: Context,
        userId: Long,
        onError: (String) -> Unit,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            this@ApplicationUserViewModel.userId = userId

            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh
                )
            }

            val userRes = getUser(
                context = context,
                userId = userId,
                include = listOf(
                    ListUsersQueryInclude.SERVERS
                ).toQueryString()
            )

            userRes
                .onSuccess { user ->
                    val isLanguageAvailable = Languages.entries.find { language -> language.code == user.attributes.language } != null
                    val ownedServers = user.attributes.relationships?.servers?.data?.filter { it.attributes.user == userId } ?: emptyList()

                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false,
                            user = user,
                            newUserEmail = TextFieldValue(user.attributes.email),
                            userUsername = TextFieldValue(user.attributes.username),
                            userFirstName = TextFieldValue(user.attributes.firstName),
                            userLastName = TextFieldValue(user.attributes.lastName),
                            userDefaultLanguage = if (isLanguageAvailable) {
                                setOf(user.attributes.language)
                            } else {
                                setOf(Languages.ENGLISH.code)
                            },
                            userIsAdmin = user.attributes.rootAdmin,
                            ownsServers = ownedServers.isNotEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch user data: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    onError("Failed to fetch user data: ${error.message}")
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

    fun setUserUsername(username: TextFieldValue) {
        _state.update {
            it.copy(
                userUsername = username
            )
        }
    }

    fun setUserFirstName(firstName: TextFieldValue) {
        _state.update {
            it.copy(
                userFirstName = firstName
            )
        }
    }

    fun setUserLastName(lastName: TextFieldValue) {
        _state.update {
            it.copy(
                userLastName = lastName
            )
        }
    }

    fun setUserDefaultLanguage(language: Set<String>) {
        _state.update {
            it.copy(
                userDefaultLanguage = language
            )
        }
    }

    fun setUserIsAdmin(isAdmin: Boolean) {
        _state.update {
            it.copy(
                userIsAdmin = isAdmin
            )
        }
    }

    fun setUserPassword(password: TextFieldValue) {
        _state.update {
            it.copy(
                userPassword = password
            )
        }
    }

    fun updateUser(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (userId == null) {
                onError("Missing User ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateRes = updateUser(
                context = context,
                userId = userId!!,
                email = state.value.newUserEmail.text.trim(),
                username = state.value.userUsername.text.trim(),
                firstName = state.value.userFirstName.text.trim(),
                lastName = state.value.userLastName.text.trim(),
                language = state.value.userDefaultLanguage.firstOrNull() ?: Languages.ENGLISH.code,
                rootAdmin = state.value.userIsAdmin,
                password = state.value.userPassword.text.trim().takeIf { it.isNotBlank() }
            )

            updateRes
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to update user: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update user: ${error.message}")
                }
        }
    }

    fun deleteUser(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (userId == null) {
                onError("Missing User ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteRes = deleteUser(
                context = context,
                userId = userId!!
            )

            deleteRes
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to delete user: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to user location: ${error.message}")
                }
        }
    }
}