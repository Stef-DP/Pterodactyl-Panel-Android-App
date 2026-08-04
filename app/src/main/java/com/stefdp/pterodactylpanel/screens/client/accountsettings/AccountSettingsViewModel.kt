package com.stefdp.pterodactylpanel.screens.client.accountsettings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientAccountSettingsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val currentTab: AccountTab = AccountTab.ACCOUNT
)

class ClientAccountSettingsViewModel : ViewModel() {
    private var _state: MutableStateFlow<ClientAccountSettingsUiState> = MutableStateFlow(ClientAccountSettingsUiState())
    val state: StateFlow<ClientAccountSettingsUiState> = _state.asStateFlow()

    fun reloadUser(
        context: Context,
        updateUser: suspend (context: Context) -> Result<User>,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = true
                )
            }

            val updateUserRes = updateUser(context)

            updateUserRes
                .onFailure { error ->
                    Logger.error("ClientActivityViewModel", "Failed to fetch user data: ${error.message}")

                    onError("Failed to fetch user data: ${error.message}")
                }

            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    fun setCurrentTab(tab: AccountTab) {
        _state.update {
            it.copy(currentTab = tab)
        }
    }
}