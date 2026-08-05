package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ActivityLog
import com.stefdp.pterodactylpanel.network.client.models.requests.GetAccountActivityQueryInclude
import com.stefdp.pterodactylpanel.network.client.models.requests.GetAccountActivityQuerySort
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccountActivityResponse
import com.stefdp.pterodactylpanel.network.client.requests.getAccountActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientAccountSettingsActivityTabUiState(
    val isLoading: Boolean = false,
    val activity: List<ActivityLog> = emptyList(),
    val page: Long = 1L,
    val pagination: GetAccountActivityResponse.Meta.Pagination? = null,
    val logToShowMetadata: String? = null,
)

class ClientAccountSettingsActivityTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientAccountSettingsActivityTabUiState> = MutableStateFlow(ClientAccountSettingsActivityTabUiState())
    val state: StateFlow<ClientAccountSettingsActivityTabUiState> = _state.asStateFlow()

    fun updateActivity(
        context: Context,
        sort: GetAccountActivityQuerySort = GetAccountActivityQuerySort.TIMESTAMP_DESC,
        page: Long = 1L,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val getActivityRes = getAccountActivity(
                context = context,
                sort = sort,
                page = page,
                include = GetAccountActivityQueryInclude.ACTOR.toString()
            )

            getActivityRes
                .onSuccess { activity ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            activity = activity.data,
                            pagination = activity.meta.pagination
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsActivityTabViewModel", "Failed to fetch account activity: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch account activity: ${error.message}")
                }
        }
    }

    fun setPage(page: Long) {
        if (page == _state.value.page) return

        _state.update {
            it.copy(
                activity = emptyList(),
                page = page
            )
        }
    }

    fun setLogToShowMetadata(logId: String?) {
        _state.update {
            it.copy(
                logToShowMetadata = logId
            )
        }
    }
}