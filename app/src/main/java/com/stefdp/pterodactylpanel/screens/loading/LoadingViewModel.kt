package com.stefdp.pterodactylpanel.screens.loading

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.utils.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoadingUiState(
    val isLogging: Boolean = false
)

class LoadingViewModel : ViewModel() {
    private val _state: MutableStateFlow<LoadingUiState> = MutableStateFlow(LoadingUiState())
    val state: StateFlow<LoadingUiState> = _state.asStateFlow()

    fun startLoading(
        context: Context,
        onError: (String?) -> Unit,
        onSuccess: (Boolean) -> Unit,
        updateLoggedUser: suspend (context: Context) -> Result<User>
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLogging = true)
            }

            val secureStore = SecureStorage.getInstance(context)

            val serverUrl = secureStore.get(SecureStorage.STORAGE_SERVER_URL_KEY)
            val clientToken = secureStore.get(SecureStorage.STORAGE_CLIENT_TOKEN_KEY)
            val applicationToken = secureStore.get(SecureStorage.STORAGE_APPLICATION_TOKEN_KEY)

            if (serverUrl == null || (clientToken == null && applicationToken == null)) {
                onError(null)

                _state.update {
                    it.copy(isLogging = false)
                }

                return@launch
            }

            val newLoggedUser = updateLoggedUser(context)

            newLoggedUser
                .onFailure { error ->
                    onError(error.message)

                    _state.update {
                        it.copy(isLogging = false)
                    }

                    return@launch
                }
                .onSuccess {
                    onSuccess(false)

                    _state.update {
                        it.copy(isLogging = false)
                    }
                }
        }
    }
}