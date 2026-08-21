package com.stefdp.pterodactylpanel.screens.application.server.tabs.details

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.requests.listUsers
import com.stefdp.pterodactylpanel.network.application.requests.updateServerDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class ApplicationServerDetailsTabUiState(
    val isLoading: Boolean = true,
    val serverName: TextFieldValue = TextFieldValue(""),
    val serverExternalIdentifier: TextFieldValue = TextFieldValue(""),
    val serverOwner: Set<String> = emptySet(),
    val serverDescription: TextFieldValue = TextFieldValue(""),
    val serverOwnerSearchQuery: String = "",
    val serverOwnerSuggestions: List<ApplicationUser> = emptyList(),
    val serverOwnerSuggestionsLoading: Boolean = false,
)

private const val TAG = "ApplicationServerDetailsTabViewModel"

class ApplicationServerDetailsTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerDetailsTabUiState> = MutableStateFlow(ApplicationServerDetailsTabUiState())
    val state: StateFlow<ApplicationServerDetailsTabUiState> = _state.asStateFlow()

    private var server: ApplicationServer? = null

    private var ownerSearchJob: kotlinx.coroutines.Job? = null

    fun init(
        context: Context,
        server: ApplicationServer?
    ) {
        this.server = server

        _state.update {
            it.copy(
                serverName = TextFieldValue(server?.attributes?.name ?: ""),
                serverExternalIdentifier = TextFieldValue(server?.attributes?.externalId ?: ""),
                serverOwner = server?.attributes?.relationships?.user?.attributes?.id
                    ?.toString()
                    ?.let { userId -> setOf(userId) }
                    ?: emptySet(),
                serverDescription = TextFieldValue(server?.attributes?.description ?: ""),
            )
        }

        setServerOwnerSearchQuery(
            context = context,
            query = server?.attributes?.relationships?.user?.attributes?.email ?: ""
        )
    }

    fun setServerName(name: TextFieldValue) {
        _state.update {
            it.copy(serverName = name)
        }
    }

    fun setServerExternalIdentifier(externalId: TextFieldValue) {
        _state.update {
            it.copy(serverExternalIdentifier = externalId)
        }
    }

    fun setServerDescription(description: TextFieldValue) {
        _state.update {
            it.copy(serverDescription = description)
        }
    }

    fun setServerOwner(
        context: Context,
        owner: Set<String>
    ) {
        _state.update {
            it.copy(
                serverOwner = owner
            )
        }

        updateOwnerQuery(context)
    }

    fun setServerOwnerSearchQuery(
        context: Context,
        query: String
    ) {
        _state.update {
            it.copy(
                serverOwnerSearchQuery = query
            )
        }

        updateOwnerQuery(context)
    }

    fun updateOwnerQuery(
        context: Context
    ) {
        ownerSearchJob?.cancel()

        _state.update {
            it.copy(
                serverOwnerSuggestionsLoading = false
            )
        }

        val currentOwner = _state.value.serverOwnerSuggestions.find { it.attributes.id.toString() in _state.value.serverOwner }

        val query = _state.value.serverOwnerSearchQuery

        if (query.length < 2 && _state.value.serverOwner.isEmpty()) {
            _state.update {
                it.copy(
                    serverOwnerSuggestions = listOfNotNull(currentOwner)
                )
            }

            return
        }

        ownerSearchJob = viewModelScope.launch {
            delay(300.milliseconds)

            _state.update {
                it.copy(
                    serverOwnerSuggestionsLoading = true
                )
            }

            val usersRes = listUsers(
                context = context,
                filterEmail = query,
            )

            val users = usersRes.getOrNull()

            if (usersRes.isFailure || users == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        serverOwnerSuggestionsLoading = false
                    )
                }

                return@launch
            }

            val usersList = if (currentOwner != null) {
                if (currentOwner.attributes.id in users.data.map { it.attributes.id }) {
                    users.data
                } else {
                    users.data + currentOwner
                }
            } else {
                users.data
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    serverOwnerSuggestions = usersList,
                    serverOwnerSuggestionsLoading = false
                )
            }
        }
    }

    fun updateServer(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (server == null) {
                onError("Missing Server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateServerRes = updateServerDetails(
                context = context,
                serverId = server!!.attributes.id,
                name = _state.value.serverName.text.trim(),
                externalId = _state.value.serverExternalIdentifier.text.trim(),
                description = _state.value.serverDescription.text.trim(),
                user = _state.value.serverOwner.firstOrNull()?.toLongOrNull() ?: server!!.attributes.user
            )

            updateServerRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to update server details: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server details: ${error.message}")
                }
        }
    }
}