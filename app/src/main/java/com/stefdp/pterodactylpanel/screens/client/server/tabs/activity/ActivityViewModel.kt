package com.stefdp.pterodactylpanel.screens.client.server.tabs.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ActivityLog
import com.stefdp.pterodactylpanel.network.client.models.requests.GetServerActivityQueryInclude
import com.stefdp.pterodactylpanel.network.client.models.requests.GetServerActivityQuerySort
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerActivityResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.getServerActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerActivityTabUiState(
    val isLoading: Boolean = false,
    val activity: List<ActivityLog> = emptyList(),
    val page: Long = 1L,
    val pagination: GetServerActivityResponse.Meta.Pagination? = null,
    val logToShowMetadata: String? = null
)

class ClientServerActivityTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerActivityTabUiState> = MutableStateFlow(ClientServerActivityTabUiState())
    val state: StateFlow<ClientServerActivityTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    fun init(server: GetServerResponse?) {
        serverId = server?.attributes?.identifier
    }

    fun updateActivity(
        context: Context,
        sort: GetServerActivityQuerySort = GetServerActivityQuerySort.TIMESTAMP_DESC,
        page: Long = 1L,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val getActivityRes = getServerActivity(
                context = context,
                serverId = serverId!!,
                sort = sort,
                page = page,
                include = GetServerActivityQueryInclude.ACTOR.toString()
            )

            getActivityRes
                .onSuccess { activity ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            activity = activity.data,
                            pagination = activity.meta.pagination,
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerActivityTabViewModel", "Failed to fetch server activity: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server activity: ${error.message}")
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