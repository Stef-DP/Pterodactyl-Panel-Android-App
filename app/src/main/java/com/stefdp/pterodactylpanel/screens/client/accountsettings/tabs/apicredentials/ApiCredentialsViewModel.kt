package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.apicredentials

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ApiKey
import com.stefdp.pterodactylpanel.network.client.requests.createAccountApiKey
import com.stefdp.pterodactylpanel.network.client.requests.deleteAccountApiKey
import com.stefdp.pterodactylpanel.network.client.requests.listAccountApiKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientAccountSettingsApiCredentialsTabUiState(
    val isLoading: Boolean = false,
    val apiKeys: List<ApiKey> = emptyList(),
    val newApiKeyDescription: TextFieldValue = TextFieldValue(""),
    val newApikeyAllowedIps: TextFieldValue = TextFieldValue(""),
    val apiKeyToDelete: String? = null
)

class ClientAccountSettingsApiCredentialsTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientAccountSettingsApiCredentialsTabUiState> = MutableStateFlow(ClientAccountSettingsApiCredentialsTabUiState())
    val state: StateFlow<ClientAccountSettingsApiCredentialsTabUiState> = _state.asStateFlow()

    fun updateApiKeys(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val apiKeys = _state.value.apiKeys

            _state.update {
                it.copy(
                    isLoading = true,
                    apiKeys = emptyList()
                )
            }

            val listApiKeys = listAccountApiKeys(
                context = context
            )

            listApiKeys
                .onSuccess { apiKeysRes ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            apiKeys = apiKeysRes.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsApiCredentialsTabViewModel", "Failed to fetch API keys: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            apiKeys = apiKeys
                        )
                    }

                    onError("Failed to fetch API keys: ${error.message}")
                }
        }
    }

    fun setNewApiKeyDescription(description: TextFieldValue) {
        _state.update {
            it.copy(
                newApiKeyDescription = description
            )
        }
    }

    fun setNewApiKeyAllowedIps(allowedIps: TextFieldValue) {
        _state.update {
            it.copy(
                newApikeyAllowedIps = allowedIps
            )
        }
    }

    fun createApikey(
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

            val createApikeyRes = createAccountApiKey(
                context = context,
                description = _state.value.newApiKeyDescription.text.trim(),
                allowedIps = _state.value.newApikeyAllowedIps.text
                    .takeIf { it.isNotBlank() }
                    ?.trim()
                    ?.split("\n")
            )

            createApikeyRes
                .onSuccess {
                    updateApiKeys(
                        context = context,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsApiCredentialsTabViewModel", "Failed to create API key: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create API key: ${error.message}")
                }
        }
    }

    fun setApiKeyToDelete(
        apiKeyId: String?,
        skipLoading: Boolean = false
    ) {
        if (apiKeyId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                apiKeyToDelete = apiKeyId
            )
        }
    }

    fun deleteApiKey(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val apiKeyId = _state.value.apiKeyToDelete

            if (apiKeyId == null) {
                onError("Missing API key ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteApiKeyRes = deleteAccountApiKey(
                    context = context,
                    keyId = apiKeyId
                )

            deleteApiKeyRes
                .onSuccess {
                    updateApiKeys(
                        context = context,
                        onSuccess = {
                            setApiKeyToDelete(null, true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsApiCredentialsTabViewModel", "Failed to delete API key: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete API key: ${error.message}")
                }
        }
    }
}