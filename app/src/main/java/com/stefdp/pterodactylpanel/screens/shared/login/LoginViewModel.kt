package com.stefdp.pterodactylpanel.screens.shared.login

import android.R.attr.tag
import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
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
    val showNotificationsPopup: Boolean = false,
    val serverUrl: TextFieldValue = TextFieldValue(""),
    val clientApiKey: TextFieldValue = TextFieldValue(""),
    val isInsecureUrl: Boolean = false,
    val hasAcknowledgedInsecureUrlWarning: Boolean = false,
)

private const val TAG = "LoginViewModel"

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun setShowNotificationsPopup(show: Boolean) {
        _state.update {
            it.copy(
                showNotificationsPopup = show
            )
        }
    }

    fun closeNotificationsPopup() {
        _state.update {
            it.copy(
                showNotificationsPopup = false
            )
        }
    }

    fun setServerUrl(url: TextFieldValue) {
        _state.update {
            it.copy(
                serverUrl = url,
                hasAcknowledgedInsecureUrlWarning = false,
                isInsecureUrl = url.text.trim().startsWith("http://")
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
            it.copy(
                clientApiKey = apiKey
            )
        }
    }

    fun onLogin(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        updateLoggedUser: suspend (context: Context) -> Result<User>
    ) {
        Logger.debug(TAG, "Starting login process")

        viewModelScope.launch {
            val serverUrl = _state.value.serverUrl.text.trim()

            val isValidUrl = DomainRegex.matches(serverUrl.lowercase()) || IPRegex.matches(serverUrl.lowercase())

            if (!isValidUrl) {
                onError("Please enter a valid server URL")

                return@launch
            }

            val clientApiKey = _state.value.clientApiKey.text.trim()

            if (!clientApiKey.startsWith("ptlc_")) {
                onError("Invalid Client API key")

                _state.update {
                    it.copy(isLoading = false)
                }

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

            onSuccess()

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }
}