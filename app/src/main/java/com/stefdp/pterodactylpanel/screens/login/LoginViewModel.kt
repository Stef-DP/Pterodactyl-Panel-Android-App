package com.stefdp.pterodactylpanel.screens.login

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val isLoading: Boolean = false,
    val serverUrl: TextFieldValue = TextFieldValue(""),
    val clientApiKey: TextFieldValue = TextFieldValue(""),
    val applicationApiKey: TextFieldValue = TextFieldValue(""),
    val isInsecureUrl: Boolean = false,
    val hasAcknowledgedInsecureUrlWarning: Boolean = false,
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()
}