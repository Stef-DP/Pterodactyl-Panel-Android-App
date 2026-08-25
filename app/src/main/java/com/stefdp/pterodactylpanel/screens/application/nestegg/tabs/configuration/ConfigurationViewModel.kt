package com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.configuration

import androidx.lifecycle.ViewModel
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ApplicationNestEggConfigurationTabUiState(
    val isLoading: Boolean = true,
    val egg: ApplicationEgg? = null
)

private const val TAG = "ApplicationNestEggConfigurationTabViewModel"

class ApplicationNestEggConfigurationTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNestEggConfigurationTabUiState> = MutableStateFlow(ApplicationNestEggConfigurationTabUiState())
    val state: StateFlow<ApplicationNestEggConfigurationTabUiState> = _state.asStateFlow()

    fun init(egg: ApplicationEgg? = null) {
        _state.update {
            it.copy(
                isLoading = false,
                egg = egg
            )
        }
    }
}