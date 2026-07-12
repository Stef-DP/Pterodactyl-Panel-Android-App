package com.stefdp.pterodactylpanel

import androidx.lifecycle.ViewModel
import com.stefdp.pterodactylpanel.network.client.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MainUiState(
    val loggedUser: User? = null
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow<MainUiState>(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()
}