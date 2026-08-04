package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.account

import android.R.attr.data
import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.network.client.requests.GetAccount2FAQrCodeResult
import com.stefdp.pterodactylpanel.network.client.requests.disableAccount2FA
import com.stefdp.pterodactylpanel.network.client.requests.enableAccount2FA
import com.stefdp.pterodactylpanel.network.client.requests.getAccount2FAQrCode
import com.stefdp.pterodactylpanel.network.client.requests.updateAccountEmail
import com.stefdp.pterodactylpanel.network.client.requests.updateAccountPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientAccountSettingsAccountTabUiState(
    val isLoading: Boolean = true,
    val currentPassword: TextFieldValue = TextFieldValue(""),
    val newPassword: TextFieldValue = TextFieldValue(""),
    val newPasswordConfirmation: TextFieldValue = TextFieldValue(""),
    val newEmail: TextFieldValue = TextFieldValue(""),
    val currentEmail: TextFieldValue = TextFieldValue(""),
    val emailPasswordConfirmation: TextFieldValue = TextFieldValue(""),
    val showEnable2FAPopup: Boolean = false,
    val twoFactorAuthenticationUri: String? = null,
    val twoFactorAuthenticationSecret: String? = null,
    val twoFactorAuthenticationCode: TextFieldValue = TextFieldValue(""),
    val twoFactorAuthenticationPassword: TextFieldValue = TextFieldValue(""),
    val recoveryCodes: List<String> = emptyList(),
    val is2FAEnabled: Boolean = false,
    val showDisable2FAPopup: Boolean = false
)

class ClientAccountSettingsAccountTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientAccountSettingsAccountTabUiState> = MutableStateFlow(ClientAccountSettingsAccountTabUiState())
    val state: StateFlow<ClientAccountSettingsAccountTabUiState> = _state.asStateFlow()

    fun init(
        context: Context,
        user: User?,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentEmail = TextFieldValue(user?.attributes?.email ?: ""),
                    newEmail = TextFieldValue(user?.attributes?.email ?: ""),
                )
            }

            Logger.debug("ClientAccountSettingsAccountTabViewModel", "Checking 2FA status for user: ${user?.attributes?.email}")

            val check2FARes = getAccount2FAQrCode(
                context = context
            )

            check2FARes
                .onSuccess { res ->
                    _state.update {
                        it.copy(
                            is2FAEnabled = res is GetAccount2FAQrCodeResult.AlreadyEnabled,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsAccountTabViewModel", "Failed to check 2FA status: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun setCurrentPassword(password: TextFieldValue) {
        _state.update {
            it.copy(
                currentPassword = password
            )
        }
    }

    fun setNewPassword(password: TextFieldValue) {
        _state.update {
            it.copy(
                newPassword = password
            )
        }
    }

    fun setNewPasswordConfirmation(password: TextFieldValue) {
        _state.update {
            it.copy(
                newPasswordConfirmation = password
            )
        }
    }

    fun setNewEmail(email: TextFieldValue) {
        _state.update {
            it.copy(
                newEmail = email
            )
        }
    }

    fun setEmailPasswordConfirmation(password: TextFieldValue) {
        _state.update {
            it.copy(
                emailPasswordConfirmation = password
            )
        }
    }

    fun updatePassword(
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

            val updatePasswordRes = updateAccountPassword(
                context = context,
                currentPassword = _state.value.currentPassword.text.trim(),
                password = _state.value.newPassword.text.trim(),
                passwordConfirmation = _state.value.newPasswordConfirmation.text.trim()
            )

            updatePasswordRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsAccountTabViewModel", "Failed to update password: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update password: ${error.message}")
                }
        }
    }

    fun updateEmail(
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

            val updateEmailRes = updateAccountEmail(
                context = context,
                email = _state.value.newEmail.text.trim(),
                password = _state.value.emailPasswordConfirmation.text.trim()
            )

            updateEmailRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsAccountTabViewModel", "Failed to update email: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update email: ${error.message}")
                }
        }
    }

    fun showEnable2FAPopup(
        context: Context,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showEnable2FAPopup = true,
                    isLoading = true
                )
            }

            val get2FACodeRes = getAccount2FAQrCode(
                context = context
            )

            get2FACodeRes
                .onSuccess { res ->
                    if (res !is GetAccount2FAQrCodeResult.Success) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                is2FAEnabled = true
                            )
                        }

                        hideEnable2FAPopup(true)

                        onError("2FA is already enabled on this account")

                        return@onSuccess
                    }

                    val uri = res.response.data.imageUrlData
                    val secret = res.response.data.secret

                    _state.update {
                        it.copy(
                            isLoading = false,
                            twoFactorAuthenticationUri = uri,
                            twoFactorAuthenticationSecret = secret
                                .chunked(4)
                                .joinToString(" ")
                        )
                    }
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsAccountTabViewModel", "Failed to fetch 2FA code: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    hideEnable2FAPopup(true)

                    onError("Failed to fetch 2FA code: ${error.message}")
                }
        }
    }

    fun hideEnable2FAPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showEnable2FAPopup = false,
                twoFactorAuthenticationSecret = null,
                twoFactorAuthenticationUri = null,
                twoFactorAuthenticationCode = TextFieldValue(""),
                twoFactorAuthenticationPassword = TextFieldValue("")
            )
        }
    }

    fun set2FACode(code: TextFieldValue) {
        _state.update {
            it.copy(
                twoFactorAuthenticationCode = code
            )
        }
    }

    fun set2FAPassword(password: TextFieldValue) {
        _state.update {
            it.copy(
                twoFactorAuthenticationPassword = password
            )
        }
    }
    
    fun enable2FA(
        context: Context,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch { 
            _state.update { 
                it.copy(
                    isLoading = true
                )
            }
            
            val enable2FARes = enableAccount2FA(
                context = context,
                code = _state.value.twoFactorAuthenticationCode.text.trim(),
                password = _state.value.twoFactorAuthenticationPassword.text.trim()
            )
            
            enable2FARes
                .onSuccess { recoveryCodesRes ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            recoveryCodes = recoveryCodesRes.attributes.tokens,
                            is2FAEnabled = true
                        )
                    }
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsAccountTabViewModel", "Failed to enable 2FA: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    hideEnable2FAPopup(true)

                    onError("Failed to enable 2FA: ${error.message}")
                }
        }
    }

    fun hideRecoveryCodes() {
        _state.update {
            it.copy(
                recoveryCodes = emptyList()
            )
        }

        hideEnable2FAPopup(true)
    }

    fun showDisable2FAPopup() {
        _state.update {
            it.copy(
                showDisable2FAPopup = true
            )
        }
    }

    fun hideDisable2FAPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showDisable2FAPopup = false,
                twoFactorAuthenticationPassword = TextFieldValue(""),
                twoFactorAuthenticationCode = TextFieldValue("")
            )
        }
    }

    // TODO: fix disable 2FA succeeding as HTTP request but not actually disabling 2FA

    fun disable2FA(
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

            val disable2FARes = disableAccount2FA(
                context = context,
                password = _state.value.twoFactorAuthenticationPassword.text.trim()
            )

            disable2FARes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            is2FAEnabled = false,
                            showDisable2FAPopup = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientAccountSettingsAccountTabViewModel", "Failed to disable 2FA: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            showDisable2FAPopup = false
                        )
                    }

                    onError("Failed to disable 2FA: ${error.message}")
                }
        }
    }
}