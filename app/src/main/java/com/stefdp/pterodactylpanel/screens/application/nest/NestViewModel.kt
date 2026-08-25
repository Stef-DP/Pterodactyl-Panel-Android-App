package com.stefdp.pterodactylpanel.screens.application.nest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNest
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestEggsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListUsersQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.getNest
import com.stefdp.pterodactylpanel.network.models.plus
import com.stefdp.pterodactylpanel.network.models.toQueryString
import com.stefdp.pterodactylpanel.screens.application.users.Languages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNestUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val nest: ApplicationNest? = null
)

private const val TAG = "ApplicationNestViewModel"

class ApplicationNestViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNestUiState> = MutableStateFlow(ApplicationNestUiState())
    val state: StateFlow<ApplicationNestUiState> = _state.asStateFlow()

    fun init(
        context: Context,
        nestId: Long,
        onError: (String) -> Unit,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh
                )
            }

            val nestRes = getNest(
                context = context,
                nestId = nestId,
                include = listOf(
                    ListNestsQueryInclude.EGGS,
                    ListNestsQueryInclude.EGGS + ListNestEggsQueryInclude.SERVERS
                ).toQueryString()
            )

            nestRes
                .onSuccess { nest ->
                   _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false,
                            nest = nest,
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch nest data: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    onError("Failed to fetch nest data: ${error.message}")
                }
        }
    }
}