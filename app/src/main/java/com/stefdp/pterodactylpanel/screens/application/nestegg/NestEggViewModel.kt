package com.stefdp.pterodactylpanel.screens.application.nestegg

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestEggsQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.getNestEgg
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNestEggUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val egg: ApplicationEgg? = null,
    val currentTab: NestEggTab = NestEggTab.VARIABLES, // TODO: set back to CONFIGURATION
    val backHistory: List<NestEggTab> = emptyList()
)

private const val TAG = "ApplicationNestEggViewModel"

class ApplicationNestEggViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNestEggUiState> = MutableStateFlow(ApplicationNestEggUiState())
    val state: StateFlow<ApplicationNestEggUiState> = _state.asStateFlow()

    fun init(
        context: Context,
        nestId: Long,
        eggId: Long,
        onReloadFinish: () -> Unit,
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

            val eggRes = getNestEgg(
                context = context,
                nestId = nestId,
                eggId = eggId,
                include = listOf(
                    ListNestEggsQueryInclude.VARIABLES
                ).toQueryString()
            )

            eggRes
                .onSuccess { egg ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            egg = egg
                        )
                    }

                    onReloadFinish()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch nest egg: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    onError("Failed to fetch nest egg: ${error.message}")
                }
        }
    }

    fun setCurrentTab(tab: NestEggTab) {
        _state.update {
            it.copy(
                currentTab = tab,
                backHistory = it.backHistory + it.currentTab
            )
        }
    }

    fun handleBack() {
        _state.update {
            val lastTab = it.backHistory.lastOrNull() ?: NestEggTab.CONFIGURATION

            it.copy(
                currentTab = lastTab,
                backHistory = it.backHistory.dropLast(1)
            )
        }
    }
}