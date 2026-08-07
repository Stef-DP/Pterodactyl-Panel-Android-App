package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.sshkeys

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.SshKey
import com.stefdp.pterodactylpanel.network.client.requests.addAccountSshKey
import com.stefdp.pterodactylpanel.network.client.requests.listAccountSshKeys
import com.stefdp.pterodactylpanel.network.client.requests.removeAccountSshKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountSettingsSshKeysTabUiState(
    val isLoading: Boolean = false,
    val sshKeys: List<SshKey> = emptyList(),
    val newSshKeyName: TextFieldValue = TextFieldValue(""),
    val newSshKeyPublicKey: TextFieldValue = TextFieldValue(""),
    val sshKeyToDelete: String? = null
)

private const val TAG = "AccountSettingsViewModel"

class AccountSettingsSshKeysTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<AccountSettingsSshKeysTabUiState> = MutableStateFlow(AccountSettingsSshKeysTabUiState())
    val state: StateFlow<AccountSettingsSshKeysTabUiState> = _state.asStateFlow()

    fun updateSshKeys(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val sshKeys = _state.value.sshKeys

            _state.update {
                it.copy(
                    isLoading = true,
                    sshKeys = emptyList()
                )
            }

            val listSshKeys = listAccountSshKeys(
                context = context
            )

            listSshKeys
                .onSuccess { sshKeysRes ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            sshKeys = sshKeysRes.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug(TAG, "Failed to fetch SSH keys: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            sshKeys = sshKeys
                        )
                    }

                    onError("Failed to fetch SSH keys: ${error.message}")
                }
        }
    }

    fun setNewSshKeyName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newSshKeyName = name
            )
        }
    }

    fun setNewSshKeyPublicKey(publicKey: TextFieldValue) {
        _state.update {
            it.copy(
                newSshKeyPublicKey = publicKey
            )
        }
    }

    fun createSshKey(
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

            val createSshKeyRes = addAccountSshKey(
                context = context,
                name = _state.value.newSshKeyName.text.trim(),
                publicKey = _state.value.newSshKeyPublicKey.text.trim()
            )

            createSshKeyRes
                .onSuccess {
                    updateSshKeys(
                        context = context,
                        onSuccess = {
                            setNewSshKeyName(TextFieldValue(""))
                            setNewSshKeyPublicKey(TextFieldValue(""))

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug(TAG, "Failed to create SSH key: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create SSH key: ${error.message}")
                }
        }
    }

    fun setSshKeyToDelete(
        sshKeyId: String?,
        skipLoading: Boolean = false
    ) {
        if (sshKeyId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                sshKeyToDelete = sshKeyId
            )
        }
    }

    fun deleteSshKey(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val sshKeyFingerprint = _state.value.sshKeyToDelete

            if (sshKeyFingerprint == null) {
                onError("Missing SSH key fingerprint")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val removeSshKeyRes = removeAccountSshKey(
                context = context,
                fingerprint = sshKeyFingerprint
            )

            removeSshKeyRes
                .onSuccess {
                    updateSshKeys(
                        context = context,
                        onSuccess = {
                            setSshKeyToDelete(null, true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug(TAG, "Failed to delete SSH key: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete SSH key: ${error.message}")
                }
        }
    }
}