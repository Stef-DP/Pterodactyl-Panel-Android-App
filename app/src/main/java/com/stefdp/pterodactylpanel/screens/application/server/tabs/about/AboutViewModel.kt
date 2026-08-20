package com.stefdp.pterodactylpanel.screens.application.server.tabs.about

import androidx.lifecycle.ViewModel
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ApplicationServerAboutTabUiState(
    val server: ApplicationServer? = null,
)

private const val TAG = "ApplicationServerAboutTabViewModel"

class ApplicationServerAboutTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerAboutTabUiState> = MutableStateFlow(ApplicationServerAboutTabUiState())
    val state: StateFlow<ApplicationServerAboutTabUiState> = _state.asStateFlow()

    private var serverId: Long? = null

    fun init(server: ApplicationServer?) {
        this.serverId = server?.attributes?.id

        _state.value = ApplicationServerAboutTabUiState(
            server = server
        )
    }
}