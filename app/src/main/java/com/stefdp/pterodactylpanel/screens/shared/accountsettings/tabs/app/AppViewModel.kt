package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.app

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.updatemanager.UpdateManager
import com.stefdp.pterodactylpanel.utils.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UpdateDownloadFolderType {
    FILE,
    BACKUP
}

data class AccountSettingsAppTabUiState(
    val updateDownloadFolderType: UpdateDownloadFolderType = UpdateDownloadFolderType.FILE,
    val hasNotificationPermission: Boolean = false,
    val biometricAuthenticationEnabled: Boolean = false,
    val isUpdateAvailable: Boolean = false,
    val isLoading: Boolean = false,
)

private const val TAG = "AccountSettingsAppTabViewModel"

class AccountSettingsAppTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<AccountSettingsAppTabUiState> = MutableStateFlow(AccountSettingsAppTabUiState())
    val state: StateFlow<AccountSettingsAppTabUiState> = _state.asStateFlow()

    fun init(
        update: Boolean
    ) {
        _state.update {
            it.copy(
                isUpdateAvailable = update
            )
        }
    }

    fun refreshBiometricAuthenticationEnabled(context: Context) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            _state.update {
                it.copy(
                    biometricAuthenticationEnabled = secureStore.get(SecureStorage.STORAGE_UNLOCK_WITH_BIOMETRICS_KEY).toBoolean()
                )
            }
        }
    }

    fun setBiometricAuthenticationEnabled(
        context: Context,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)
            secureStore.set(SecureStorage.STORAGE_UNLOCK_WITH_BIOMETRICS_KEY, enabled.toString())

            _state.update {
                it.copy(
                    biometricAuthenticationEnabled = enabled
                )
            }
        }
    }

    fun setUpdateDownloadFolderType(type: UpdateDownloadFolderType) {
        _state.update {
            it.copy(
                updateDownloadFolderType = type
            )
        }
    }

    fun setHasNotificationPermission(hasPermission: Boolean) {
        _state.update {
            it.copy(hasNotificationPermission = hasPermission)
        }
    }

    fun updateDownloadFolder(
        context: Context,
        uri: Uri,
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val key = when (_state.value.updateDownloadFolderType) {
                UpdateDownloadFolderType.FILE -> SecureStorage.STORAGE_FILE_DOWNLOAD_FOLDER_KEY
                UpdateDownloadFolderType.BACKUP -> SecureStorage.STORAGE_BACKUP_DOWNLOAD_FOLDER_KEY
            }

            secureStore.set(key, uri.toString())
        }
    }

    fun checkForUpdates(
        updateManager: UpdateManager,
        onSuccess: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val hasUpdate = updateManager.checkForUpdates(
                openStore = true
            )

            onSuccess(hasUpdate)

            _state.update {
                it.copy(
                    isLoading = false,
                    isUpdateAvailable = hasUpdate
                )
            }
        }
    }

    fun downloadUpdate(
        updateManager: UpdateManager,
    ) {
        viewModelScope.launch {
            updateManager.update()
        }
    }

    fun logout(
        context: Context,
        navController: NavHostController,
        localUpdateLoggedUser: suspend (context: Context) -> Result<User>,
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.del(SecureStorage.STORAGE_CLIENT_TOKEN_KEY)
            secureStore.del(SecureStorage.STORAGE_SERVER_URL_KEY)

            localUpdateLoggedUser(context)

            navController.navigate(LoginScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
}