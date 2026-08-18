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
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationNodeServersTabUiState(
    val servers: List<ApplicationServer> = emptyList(),
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
}