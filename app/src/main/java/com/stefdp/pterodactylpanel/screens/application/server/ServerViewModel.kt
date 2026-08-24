package com.stefdp.pterodactylpanel.screens.application.server

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServerDatabasesQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServersQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.getServer
import com.stefdp.pterodactylpanel.network.models.plus
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationServerUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val server: ApplicationServer? = null,
    val currentTab: ServerTab = ServerTab.MANAGE // TODO: set back to ABOUT
)

private const val TAG = "ApplicationServerViewModel"

class ApplicationServerViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerUiState> = MutableStateFlow(ApplicationServerUiState())
    val state: StateFlow<ApplicationServerUiState> = _state.asStateFlow()

    private var serverId: Long? = null

    fun init(
        context: Context,
        serverId: Long,
        onReloadFinish: () -> Unit,
        onError: (String) -> Unit,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            this@ApplicationServerViewModel.serverId = serverId

            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh
                )
            }

            val serverRes = getServer(
                context = context,
                serverId = serverId,
                include = listOf(
                    ListServersQueryInclude.NODE,
                    ListServersQueryInclude.USER,
                    ListServersQueryInclude.VARIABLES,
                    ListServersQueryInclude.NEST,
                    ListServersQueryInclude.EGG,
                    ListServersQueryInclude.ALLOCATIONS,
                    ListServersQueryInclude.DATABASES,
                    ListServersQueryInclude.DATABASES + ListServerDatabasesQueryInclude.HOST
                ).toQueryString()
            )

            serverRes
                .onSuccess { server ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            server = server
                        )
                    }

                    onReloadFinish()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    onError("Failed to fetch server: ${error.message}")
                }
        }
    }

    fun setCurrentTab(tab: ServerTab) {
        _state.update {
            it.copy(currentTab = tab)
        }
    }
}