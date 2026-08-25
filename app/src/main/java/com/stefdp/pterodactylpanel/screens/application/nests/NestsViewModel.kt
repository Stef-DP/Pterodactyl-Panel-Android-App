package com.stefdp.pterodactylpanel.screens.application.nests

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNest
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNestsResponse
import com.stefdp.pterodactylpanel.network.application.requests.listNests
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNestsUiState(
    val isRefreshing: Boolean = false,
    val nests: List<ApplicationNest>? = null,
    val page: Long = 1L,
    val pagination: ListNestsResponse.Meta.Pagination? = null
)

private const val TAG = "ApplicationNestsViewModel"

class ApplicationNestsViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNestsUiState> = MutableStateFlow(ApplicationNestsUiState())
    val state: StateFlow<ApplicationNestsUiState> = _state.asStateFlow()

    fun updateData(
        context: Context,
        isRefresh: Boolean = false,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    nests = null,
                    isRefreshing = isRefresh
                )
            }

            val usersRes = listNests(
                context = context,
                page = _state.value.page,
                include = listOf(
                    ListNestsQueryInclude.SERVERS,
                    ListNestsQueryInclude.EGGS
                ).toQueryString()
            )

            val nests = usersRes.getOrNull()

            _state.update {
                it.copy(
                    nests = nests?.data ?: emptyList(),
                    pagination = nests?.meta?.pagination,
                    isRefreshing = false
                )
            }
        }
    }

    fun setPage(page: Long) {
        if (page == _state.value.page) return

        _state.update {
            it.copy(
                nests = null,
                page = page
            )
        }
    }
}