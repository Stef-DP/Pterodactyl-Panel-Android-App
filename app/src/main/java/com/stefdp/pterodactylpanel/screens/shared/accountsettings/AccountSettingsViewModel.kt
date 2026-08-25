package com.stefdp.pterodactylpanel.screens.shared.accountsettings

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

data class AccountSettingsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val currentTab: AccountTab = AccountTab.ACCOUNT,
    val backHistory: List<AccountTab> = emptyList()
)

private const val TAG = "AccountSettingsViewModel"

class AccountSettingsViewModel : ViewModel() {
    private var _state: MutableStateFlow<AccountSettingsUiState> = MutableStateFlow(AccountSettingsUiState())
    val state: StateFlow<AccountSettingsUiState> = _state.asStateFlow()

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
                    Logger.error(TAG, "Failed to fetch user data: ${error.message}")

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
            it.copy(
                currentTab = tab,
                backHistory = it.backHistory + it.currentTab
            )
        }
    }

    fun handleBack() {
        _state.update {
            val lastTab = it.backHistory.lastOrNull() ?: AccountTab.ACCOUNT

            it.copy(
                currentTab = lastTab,
                backHistory = it.backHistory.dropLast(1)
            )
        }
    }
}