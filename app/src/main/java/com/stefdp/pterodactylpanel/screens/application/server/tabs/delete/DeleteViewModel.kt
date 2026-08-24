package com.stefdp.pterodactylpanel.screens.application.server.tabs.delete

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.requests.deleteServer
import com.stefdp.pterodactylpanel.network.application.requests.forceDeleteServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationServerDeleteTabUiState(
    val isLoading: Boolean = true,
    val showSafeDeletePopup: Boolean = false,
    val showForceDeletePopup: Boolean = false,
)

private const val TAG = "ApplicationServerDeleteTabViewModel"

class ApplicationServerDeleteTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerDeleteTabUiState> = MutableStateFlow(ApplicationServerDeleteTabUiState())
    val state: StateFlow<ApplicationServerDeleteTabUiState> = _state.asStateFlow()

    private var serverId: Long? = null

    fun init(server: ApplicationServer?) {
        this.serverId = server?.attributes?.id

        _state.update {
            it.copy(
                isLoading = false
            )
        }
    }

    fun showSafeDeletePopup() {
        _state.update {
            it.copy(
                showSafeDeletePopup = true
            )
        }
    }

    fun hideSafeDeletePopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showSafeDeletePopup = false
            )
        }
    }

    fun safelyDeleteServer(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing Server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val safelyDeleteServerRes = deleteServer(
                context = context,
                serverId = serverId!!
            )

            safelyDeleteServerRes
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to safely delete server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to safely delete server: ${error.message}")
                }
        }
    }

    fun showForceDeletePopup() {
        _state.update {
            it.copy(
                showForceDeletePopup = true
            )
        }
    }

    fun hideForceDeletePopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showForceDeletePopup = false
            )
        }
    }

    fun forciblyDeleteServer(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing Server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val forciblyDeleteServerRes = forceDeleteServer(
                context = context,
                serverId = serverId!!
            )

            forciblyDeleteServerRes
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to forcibly delete server: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to forcibly delete server: ${error.message}")
                }
        }
    }
}