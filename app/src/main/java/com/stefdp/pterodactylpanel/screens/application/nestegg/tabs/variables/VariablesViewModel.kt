package com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.variables

import androidx.lifecycle.ViewModel
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ApplicationNestEggVariablesTabUiState(
    val isLoading: Boolean = true,
    val egg: ApplicationEgg? = null
)

private const val TAG = "ApplicationNestEggVariablesTabViewModel"

class ApplicationNestEggVariablesTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNestEggVariablesTabUiState> = MutableStateFlow(ApplicationNestEggVariablesTabUiState())
    val state: StateFlow<ApplicationNestEggVariablesTabUiState> = _state.asStateFlow()

    fun init(egg: ApplicationEgg? = null) {
        _state.update {
            it.copy(
                isLoading = false,
                egg = egg
            )
        }
    }
}