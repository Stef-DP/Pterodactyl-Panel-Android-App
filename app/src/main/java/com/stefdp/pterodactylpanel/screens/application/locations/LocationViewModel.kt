package com.stefdp.pterodactylpanel.screens.application.locations

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationLocation
import com.stefdp.pterodactylpanel.network.application.models.requests.ListLocationsQuerySort
import com.stefdp.pterodactylpanel.network.application.models.responses.ListLocationsResponse
import com.stefdp.pterodactylpanel.network.application.requests.createLocation
import com.stefdp.pterodactylpanel.network.application.requests.listLocations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationLocationsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val locations: List<ApplicationLocation>? = null,
    val page: Long = 1,
    val pagination: ListLocationsResponse.Meta.Pagination? = null,
    val showCreateLocationPopup: Boolean = false,
    val newLocationShortCode: TextFieldValue = TextFieldValue(""),
    val newLocationDescription: TextFieldValue = TextFieldValue(""),
)

private const val TAG = "ApplicationLocationsViewModel"

class ApplicationLocationsViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationLocationsUiState> = MutableStateFlow(ApplicationLocationsUiState())
    val state: StateFlow<ApplicationLocationsUiState> = _state.asStateFlow()

    fun updateData(
        context: Context,
        filterShort: String? = null,
        filterLong: String? = null,
        sort: ListLocationsQuerySort? = null,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    locations = null,
                    isRefreshing = isRefresh
                )
            }

            val locationsRes = listLocations(
                context = context,
                filterShort = filterShort,
                filterLong = filterLong,
                sort = sort,
                page = _state.value.page,
            )

            val locations = locationsRes.getOrNull()

            _state.update {
                it.copy(
                    locations = locations?.data ?: emptyList(),
                    pagination = locations?.meta?.pagination,
                    isRefreshing = false,
                    isLoading = false
                )
            }
        }
    }

    fun setPage(page: Long) {
        if (page == _state.value.page) return

        _state.update {
            it.copy(
                locations = null,
                page = page
            )
        }
    }

    fun showCreateLocationPopup() {
        _state.update {
            it.copy(
                showCreateLocationPopup = true
            )
        }
    }

    fun hideCreateLocationPopup(
        skipLoading: Boolean = false
    ) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateLocationPopup = false
            )
        }
    }

    fun setNewLocationShortCode(value: TextFieldValue) {
        _state.update {
            it.copy(
                newLocationShortCode = value
            )
        }
    }

    fun setNewLocationDescription(value: TextFieldValue) {
        _state.update {
            it.copy(
                newLocationDescription = value
            )
        }
    }

    fun createLocation(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val createLocationRes = createLocation(
                context = context,
                short = _state.value.newLocationShortCode.text.trim(),
                long = _state.value.newLocationDescription.text.trim()
            )

            createLocationRes
                .onSuccess {
                    updateData(
                        context = context,
                    )

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to create location: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create location: ${error.message}")
                }
        }
    }
}