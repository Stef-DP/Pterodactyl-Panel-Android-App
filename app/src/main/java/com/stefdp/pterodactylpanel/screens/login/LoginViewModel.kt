package com.stefdp.pterodactylpanel.screens.login

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.requests.listUsers
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.utils.DomainRegex
import com.stefdp.pterodactylpanel.utils.IPRegex
import com.stefdp.pterodactylpanel.utils.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val serverUrl: TextFieldValue = TextFieldValue(""),
    val clientApiKey: TextFieldValue = TextFieldValue(""),
    val applicationApiKey: TextFieldValue = TextFieldValue(""),
    val isInsecureUrl: Boolean = false,
    val hasAcknowledgedInsecureUrlWarning: Boolean = false,
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun setServerUrl(url: TextFieldValue) {
        _state.update {
            it.copy(
                serverUrl = url,
                hasAcknowledgedInsecureUrlWarning = false,
                isInsecureUrl = url.text.startsWith("http://")
            )
        }
    }

    fun setHasAcknowledgedInsecureUrlWarning(acknowledged: Boolean) {
        _state.update {
            it.copy(
                hasAcknowledgedInsecureUrlWarning = acknowledged
            )
        }
    }

    fun setClientApiKey(apiKey: TextFieldValue) {
        _state.update {
            it.copy(clientApiKey = apiKey)
        }
    }

    fun setApplicationApiKey(apiKey: TextFieldValue) {
        _state.update {
            it.copy(applicationApiKey = apiKey)
        }
    }

    fun onLogin(
        context: Context,
        onSuccess: (
            hasClientApiKey: Boolean,
            hasApplicationApiKey: Boolean,
        ) -> Unit,
        onError: (String) -> Unit,
        updateLoggedUser: suspend (context: Context) -> Result<User>
    ) {
        val tag = "LoginViewModel"

        Logger.debug(tag, "Starting login process")

        viewModelScope.launch {
            val serverUrl = _state.value.serverUrl.text

            val isValidUrl = DomainRegex.matches(serverUrl.lowercase()) || IPRegex.matches(serverUrl.lowercase())

            if (!isValidUrl) {
                onError("Please enter a valid server URL")

                return@launch
            }

            _state.update {
                it.copy(isLoading = true)
            }

            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(
                SecureStorage.STORAGE_SERVER_URL_KEY,
                if (serverUrl.endsWith("/")) serverUrl.lowercase().dropLast(1) else serverUrl.lowercase()
            )

            val clientApiKey = _state.value.clientApiKey.text
            var isClientApiKeyValid = false

            if (clientApiKey.isNotBlank()) {
                if (!clientApiKey.startsWith("ptlc_")) {
                    onError("Invalid Client API key")

                    secureStore.del(SecureStorage.STORAGE_SERVER_URL_KEY)

                    _state.update {
                        it.copy(isLoading = false)
                    }

                    return@launch
                }

                secureStore.set(SecureStorage.STORAGE_CLIENT_TOKEN_KEY, clientApiKey)

                val userStatsRes = updateLoggedUser(context)

                if (userStatsRes.isFailure) {
                    onError("Invalid Client API key")

                    secureStore.del(SecureStorage.STORAGE_SERVER_URL_KEY)
                    secureStore.del(SecureStorage.STORAGE_CLIENT_TOKEN_KEY)

                    _state.update {
                        it.copy(isLoading = false)
                    }

                    return@launch
                }

                isClientApiKeyValid = true
            }

            val applicationApiKey = _state.value.applicationApiKey.text
            var isApplicationApiKeyValid = false

            if (applicationApiKey.isNotBlank()) {
                if (!applicationApiKey.startsWith("ptla_")) {
                    onError("Invalid Application API key")

                    secureStore.del(SecureStorage.STORAGE_SERVER_URL_KEY)

                    _state.update {
                        it.copy(isLoading = false)
                    }

                    return@launch
                }

                secureStore.set(SecureStorage.STORAGE_APPLICATION_TOKEN_KEY, applicationApiKey)

                val listUsersRes = listUsers(
                    context = context,
                    perPage = 1
                )

                if (listUsersRes.isFailure) {
                    onError("Invalid Application API key")

                    secureStore.del(SecureStorage.STORAGE_SERVER_URL_KEY)
                    secureStore.del(SecureStorage.STORAGE_APPLICATION_TOKEN_KEY)

                    _state.update {
                        it.copy(isLoading = false)
                    }

                    return@launch
                }

                isApplicationApiKeyValid = true
            }

            onSuccess(
                isClientApiKeyValid,
                isApplicationApiKeyValid
            )

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }
}