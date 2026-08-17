package com.stefdp.pterodactylpanel.screens.application.node.tabs.servers

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNest
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestsQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.listNests
import com.stefdp.pterodactylpanel.network.application.requests.listUsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNodeServersTabUiState(
    val servers: List<ApplicationServer> = emptyList(),
    val users: List<ApplicationUser> = emptyList(),
    val nests: List<ApplicationNest> = emptyList(),
    val egg: List<ApplicationEgg> = emptyList()
)

private const val TAG = "ApplicationNodeServersTabViewModel"

class ApplicationNodeServersTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationNodeServersTabUiState> = MutableStateFlow(ApplicationNodeServersTabUiState())
    val state: StateFlow<ApplicationNodeServersTabUiState> = _state.asStateFlow()

    private var nodeId: Long? = null

    fun init(node: ApplicationNode?) {
        nodeId = node?.attributes?.id

        _state.update {
            it.copy(
                servers = node?.attributes?.relationships?.servers?.data ?: emptyList()
            )
        }
    }

    fun listAllUsers(
        context: Context
    ) {
        viewModelScope.launch {
            var currentPage = 1L
            var hasNextPage = true

            while (hasNextPage) {
                val usersRes = listUsers(
                    context = context,
                    page = currentPage
                )

                if (usersRes.isFailure) break

                val users = usersRes.getOrNull() ?: break

                _state.update {
                    it.copy(
                        users = it.users + users.data
                    )
                }

                val nextLink = users.meta.pagination.links.next

                if (!nextLink.isNullOrEmpty()) {
                    currentPage++
                } else {
                    hasNextPage = false
                }
            }
        }
    }

    fun listAllNests(
        context: Context
    ) {
        viewModelScope.launch {
            var currentPage = 1L
            var hasNextPage = true

            while (hasNextPage) {
                val nestsRes = listNests(
                    context = context,
                    page = currentPage,
                    include = ListNestsQueryInclude.toQueryString(
                        ListNestsQueryInclude.EGGS
                    )
                )

                if (nestsRes.isFailure) break

                val nests = nestsRes.getOrNull() ?: break

                _state.update {
                    it.copy(
                        nests = it.nests + nests.data
                    )
                }

                val nextLink = nests.meta.pagination.links.next

                if (!nextLink.isNullOrEmpty()) {
                    currentPage++
                } else {
                    hasNextPage = false
                }
            }
        }
    }
}