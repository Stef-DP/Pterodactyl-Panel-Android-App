package com.stefdp.pterodactylpanel.screens.application.location

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationLocation
import com.stefdp.pterodactylpanel.network.application.models.requests.ListLocationsQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.deleteLocation
import com.stefdp.pterodactylpanel.network.application.requests.getLocation
import com.stefdp.pterodactylpanel.network.application.requests.updateLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationLocationUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val location: ApplicationLocation? = null,
    val shortCodeInput: TextFieldValue = TextFieldValue(""),
    val descriptionInput: TextFieldValue = TextFieldValue(""),
)

private const val TAG = "ApplicationLocationViewModel"

class ApplicationLocationViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationLocationUiState> = MutableStateFlow(ApplicationLocationUiState())
    val state: StateFlow<ApplicationLocationUiState> = _state.asStateFlow()

    private var locationId: Long? = null

    fun init(
        context: Context,
        locationId: Long,
        onError: (String) -> Unit,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            this@ApplicationLocationViewModel.locationId = locationId

            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh,
                )
            }

            val locationRes = getLocation(
                context = context,
                locationId = locationId,
                include = ListLocationsQueryInclude.toQueryString(
                    ListLocationsQueryInclude.NODES,
                    ListLocationsQueryInclude.SERVERS
                )
            )

            locationRes
                .onSuccess { location ->
                    _state.update {
                        it.copy(
                            location = location,
                            isRefreshing = false,
                            isLoading = false,
                            shortCodeInput = TextFieldValue(location.attributes.short),
                            descriptionInput = TextFieldValue(location.attributes.long ?: "")
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch location data: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    onError("Failed to fetch location data: ${error.message}")
                }
        }
    }

    fun setShortCodeInput(value: TextFieldValue) {
        _state.update {
            it.copy(shortCodeInput = value)
        }
    }

    fun setDescriptionInput(value: TextFieldValue) {
        _state.update {
            it.copy(descriptionInput = value)
        }
    }

    fun updateLocation(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (locationId == null) {
                onError("Missing Location ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateRes = updateLocation(
                context = context,
                locationId = locationId!!,
                short = state.value.shortCodeInput.text.trim(),
                long = state.value.descriptionInput.text.trim()
            )

            updateRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to update location: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update location: ${error.message}")
                }
        }
    }

    fun deleteLocation(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (locationId == null) {
                onError("Missing Location ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteRes = deleteLocation(
                context = context,
                locationId = locationId!!
            )

            deleteRes
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to delete location: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete location: ${error.message}")
                }
        }
    }
}