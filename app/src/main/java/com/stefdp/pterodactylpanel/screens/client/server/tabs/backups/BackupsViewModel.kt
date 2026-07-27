package com.stefdp.pterodactylpanel.screens.client.server.tabs.backups

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerBackup
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.createServerBackup
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerBackup
import com.stefdp.pterodactylpanel.network.client.requests.downloadServerBackup
import com.stefdp.pterodactylpanel.network.client.requests.listServerBackups
import com.stefdp.pterodactylpanel.network.client.requests.restoreServerBackup
import com.stefdp.pterodactylpanel.network.client.requests.toggleServerBackupLock
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.utils.StorageUtil
import com.stefdp.pterodactylpanel.utils.getDisplayPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ClientServerBackupsTabUiState(
    val isLoading: Boolean = false,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val backups: List<ServerBackup> = emptyList(),
    val selectedUri: Uri? = null,
    val showCreateBackupPopup: Boolean = false,
    val newBackupName: TextFieldValue = TextFieldValue(""),
    val newBackupIgnoredFiles: TextFieldValue = TextFieldValue(""),
    val newBackupLocked: Boolean = false,
    val backupToRestore: String? = null,
    val restoreDeleteAllFiles: Boolean = false,
    val backupToUnlock: String? = null,
    val backupToDelete: String? = null,
)

class ClientServerBackupsTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerBackupsTabUiState> = MutableStateFlow(ClientServerBackupsTabUiState())
    val state: StateFlow<ClientServerBackupsTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    private var selectedPath: String? = null

    fun init(
        context: Context,
        server: GetServerResponse?
    ) {
        serverId = server?.attributes?.identifier

        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val backupDownloadFolder = secureStore.get(SecureStorage.STORAGE_BACKUP_DOWNLOAD_FOLDER_KEY)

            val backupDownloadFolderUri = backupDownloadFolder?.toUri()

            _state.update {
                it.copy(
                    selectedUri = backupDownloadFolderUri,
                    isServerOwner = server?.meta?.isServerOwner ?: false,
                    userPermissions = server?.meta?.userPermissions ?: emptyList()
                )
            }
        }
    }

    fun updateBackups(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val listBackupsRes = listServerBackups(
                context = context,
                serverId = serverId!!,
            )

            listBackupsRes
                .onSuccess { backups ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            backups = backups.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerBackupsTabViewModel", "Failed to fetch server backups: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server backups: ${error.message}")
                }
        }
    }

    fun performDownload(
        context: Context,
        backup: ServerBackup,
        uri: Uri,
        sendNotification: (content: @Composable () -> Unit) -> Unit,
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                sendNotification {
                    Text(
                        text = "Missing server ID",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                return@launch
            }

            val fileFits = withContext(Dispatchers.IO) {
                StorageUtil.canFitFile(
                    context = context,
                    uri = uri,
                    fileSize = backup.attributes.bytes
                )
            }

            if (!fileFits) {
                sendNotification {
                    Text(
                        text = "Not enough space in the selected directory to download the backup",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                return@launch
            }

            val fileFitsCache = withContext(Dispatchers.IO) {
                StorageUtil.canFitInternalCache(
                    context = context,
                    fileSize = backup.attributes.bytes
                )
            }

            if (!fileFitsCache) {
                sendNotification {
                    Text(
                        text = "Not enough space in the internal cache to download the backup",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                return@launch
            }

            sendNotification {
                Text(
                    text = "Starting download...",
                )
            }

            val fileName = backup.attributes.name + ".tar.gz"

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            if (tempFile.exists()) {
                withContext(Dispatchers.IO) {
                    tempFile.delete()
                }
            }

            withContext(Dispatchers.IO) {
                val downloadRes = downloadServerBackup(
                    context = context,
                    serverId = serverId!!,
                    backupId = backup.attributes.uuid,
                    destinationPath = tempDestinationPath,
                    notificationTitle = "Downloading backup",
                    notificationContent = "Downloading ${backup.attributes.name}",
                )

                downloadRes
                    .onSuccess {
                        try {
                            val docUri =
                                DocumentsContract.buildDocumentUriUsingTree(
                                    uri,
                                    DocumentsContract.getTreeDocumentId(
                                        uri
                                    )
                                )

                            val fileUri = DocumentsContract.createDocument(
                                context.contentResolver,
                                docUri,
                                "application/octet-stream",
                                fileName
                            )

                            if (fileUri != null) {
                                context.contentResolver.openOutputStream(fileUri)
                                    ?.use { out ->
                                        tempFile.inputStream().use { inp ->
                                            inp.copyTo(out)
                                        }
                                    }

                                sendNotification {
                                    Text(
                                        text = "Backup downloaded to ${selectedPath}/$fileName",
                                    )
                                }
                            } else {
                                sendNotification {
                                    Text(
                                        text = "Failed to create file in selected directory",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Logger.error(
                                "ClientServerBackupsTabViewModel",
                                "Failed to copy file to selected directory",
                                e
                            )

                            sendNotification {
                                Text(
                                    text = "Failed to copy file to selected directory: ${e.message}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } finally {
                            tempFile.delete()
                        }
                    }
                    .onFailure {
                        Logger.error("ClientServerBackupsTabViewModel", "Failed to download backup", it)

                        sendNotification {
                            Text(
                                text = "Failed to download backup: ${it.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
            }
        }
    }

    fun setSelectedUri(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(SecureStorage.STORAGE_BACKUP_DOWNLOAD_FOLDER_KEY, uri.toString())

            _state.update {
                it.copy(
                    selectedUri = uri
                )
            }

            selectedPath = getDisplayPath(uri)
        }
    }

    fun showCreateBackupPopup() {
        _state.update {
            it.copy(
                showCreateBackupPopup = true
            )
        }
    }

    fun hideCreateBackupPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateBackupPopup = false
            )
        }
    }

    fun setBackupToRestore(
        backupId: String?,
        skipLoading: Boolean = false
    ) {
        if (backupId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                backupToRestore = backupId
            )
        }
    }

    fun setRestoreDeleteAllFiles(deleteAllFiles: Boolean) {
         _state.update {
            it.copy(
                restoreDeleteAllFiles = deleteAllFiles
            )
        }
    }

    fun setBackupToUnlock(
        backupId: String?,
        skipLoading: Boolean = false
    ) {
        if (backupId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                backupToUnlock = backupId
            )
        }
    }

    fun setBackupToDelete(
        backupId: String?,
        skipLoading: Boolean = false
    ) {
        if (backupId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                backupToDelete = backupId
            )
        }
    }

    fun setNewBackupName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newBackupName = name
            )
        }
    }

    fun setNewBackupIgnoredFiles(ignoredFiles: TextFieldValue) {
        _state.update {
            it.copy(
                newBackupIgnoredFiles = ignoredFiles
            )
        }
    }

    fun setNewBackupLocked(locked: Boolean) {
        _state.update {
            it.copy(
                newBackupLocked = locked
            )
        }
    }

    fun restoreBackup(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val backupUuid = _state.value.backupToRestore

            if (backupUuid == null) {
                onError("Missing backup UUID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val restoreBackupRes = restoreServerBackup(
                context = context,
                serverId = serverId!!,
                backupId = backupUuid,
                truncate = _state.value.restoreDeleteAllFiles
            )

            restoreBackupRes
                .onSuccess {
                    updateBackups(
                        context = context,
                        onSuccess = {
                            setBackupToRestore(null, true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerBackupsTabViewModel", "Failed to restore server backup: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to restore server backup: ${error.message}")
                }
        }
    }

    fun toggleBackupLock(
        context: Context,
        onSuccess: (action: String) -> Unit,
        onError: (String) -> Unit,
        backup: ServerBackup? = null
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val backupToToggle = backup ?: _state.value.backups.find { it.attributes.uuid == _state.value.backupToUnlock }

            if (backupToToggle == null) {
                onError("Missing backup UUID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val toggleBackupRes = toggleServerBackupLock(
                context = context,
                serverId = serverId!!,
                backupId = backupToToggle.attributes.uuid
            )

            toggleBackupRes
                .onSuccess {
                    updateBackups(
                        context = context,
                        onSuccess = {
                            val action = if (backupToToggle.attributes.isLocked) "unlocked" else "locked"

                            setBackupToUnlock(null, true)

                            onSuccess(action)
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    val action = if (backupToToggle.attributes.isLocked) "unlock" else "lock"

                    Logger.debug("ClientServerBackupsTabViewModel", "Failed to $action server backup: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to $action server backup: ${error.message}")
                }
        }
    }

    fun deleteBackup(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val backupUuid = _state.value.backupToDelete

            if (backupUuid == null) {
                onError("Missing backup UUID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteBackupRes = deleteServerBackup(
                context = context,
                serverId = serverId!!,
                backupId = backupUuid
            )

            deleteBackupRes
                .onSuccess {
                    updateBackups(
                        context = context,
                        onSuccess = {
                            setBackupToDelete(null, true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerBackupsTabViewModel", "Failed to restore server backup: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to restore server backup: ${error.message}")
                }
        }
    }

    fun createBackup(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val createBackupRes = createServerBackup(
                context = context,
                serverId = serverId!!,
                name = _state.value.newBackupName.text.ifEmpty { null },
                ignoredFiles = _state.value.newBackupIgnoredFiles.text.ifEmpty { null },
                isLocked = _state.value.newBackupLocked
            )

            createBackupRes
                .onSuccess {
                    updateBackups(
                        context = context,
                        onSuccess = {
                            hideCreateBackupPopup(true)

                            onSuccess()
                        },
                        onError = onError
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerBackupsTabViewModel", "Failed to create server backup: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create server backup: ${error.message}")
                }
        }
    }
}