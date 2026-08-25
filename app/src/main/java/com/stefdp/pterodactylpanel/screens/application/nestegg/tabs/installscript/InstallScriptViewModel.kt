package com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.installscript

import androidx.lifecycle.ViewModel
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ApplicationNestEggInstallScriptTabUiState(
    val isLoading: Boolean = true,
    val egg: ApplicationEgg? = null
)

private const val TAG = "ApplicationNestEggInstallScriptTabViewModel"

class ApplicationNestEggInstallScriptTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNestEggInstallScriptTabUiState> = MutableStateFlow(ApplicationNestEggInstallScriptTabUiState())
    val state: StateFlow<ApplicationNestEggInstallScriptTabUiState> = _state.asStateFlow()

    fun init(egg: ApplicationEgg? = null) {
        _state.update {
            it.copy(
                isLoading = false,
                egg = egg
            )
        }
    }
}