package com.stefdp.pterodactylpanel.screens.client.server.tabs.files

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
import com.stefdp.pterodactylpanel.network.client.models.ServerFile
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.requests.RenameServerFilesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerFilesPermissionsBody
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.UploadFile
import com.stefdp.pterodactylpanel.network.client.requests.compressServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.copyServerFile
import com.stefdp.pterodactylpanel.network.client.requests.createServerFolder
import com.stefdp.pterodactylpanel.network.client.requests.decompressServerFile
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.downloadServerFile
import com.stefdp.pterodactylpanel.network.client.requests.getServerFileContents
import com.stefdp.pterodactylpanel.network.client.requests.listServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.renameServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.updateServerFilesPermissions
import com.stefdp.pterodactylpanel.network.client.requests.uploadServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.writeServerFile
import com.stefdp.pterodactylpanel.ui.theme.HighlightLanguage
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.utils.StorageUtil
import com.stefdp.pterodactylpanel.utils.copyUriToTempFile
import com.stefdp.pterodactylpanel.utils.getDisplayPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ClientServerFilesTabUiState(
    val isLoading: Boolean = false,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
    val files: List<ServerFile> = emptyList(),
    val selectedFiles: List<String> = emptyList(),
    val filesPath: List<String> = listOf(
        "home",
        "container",
    ),
    val showNewDirectoryPopup: Boolean = false,
    val newDirectoryName: TextFieldValue = TextFieldValue(""),
    val showMoveFilesPopup: Boolean = false,
    val isRename: Boolean = false,
    val showDeleteFilesPopup: Boolean = false,
    val showUpdatePermissionsPopup: Boolean = false,
    val newPermissions: TextFieldValue = TextFieldValue(""),
    val selectedUri: Uri? = null,
    val isUploading: Boolean = false,
    val uploadPercent: Float = 0f,
    val fileToEdit: String? = null,
    val isFetchingFileContent: Boolean = false,
    val fileContent: TextFieldValue = TextFieldValue(""),
    val originalFileContent: String = "",
    val selectedLanguage: HighlightLanguage = HighlightLanguage.PLAIN_TEXT,
    val createNewFile: Boolean = false,
    val newFileName: TextFieldValue = TextFieldValue(""),
    val showUnsavedFileWarningPopup: Boolean = false,
    val isFileSaving: Boolean = false,
    val showNewFileNamePopup: Boolean = false,
)

class ClientServerFilesTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerFilesTabUiState> = MutableStateFlow(ClientServerFilesTabUiState())
    val state: StateFlow<ClientServerFilesTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    private var selectedPath: String? = null

    fun init(
        context: Context,
        server: GetServerResponse?,
        directory: String?
    ) {
        this.serverId = server?.attributes?.identifier

        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val fileDownloadFolder = secureStore.get(SecureStorage.STORAGE_FILE_DOWNLOAD_FOLDER_KEY)

            val fileDownloadFolderUri = fileDownloadFolder?.toUri()

            _state.update {
                it.copy(
                    selectedUri = fileDownloadFolderUri,
                    isServerOwner = server?.meta?.isServerOwner ?: false,
                    userPermissions = server?.meta?.userPermissions ?: emptyList()
                )
            }

            if (directory != null) {
                _state.update {
                    it.copy(
                        filesPath = listOf(
                            "home",
                            "container",
                        ) + directory.split("/").filter { path -> path.isNotBlank() }
                    )
                }
            }
        }
    }

    fun updateFiles(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit = { }
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            var path = (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            if (path.isBlank()) {
                path = "/"
            }

            val serverFilesRes = listServerFiles(
                context = context,
                serverId = serverId!!,
                directory = path
            )

            serverFilesRes
                .onSuccess { files ->
                    _state.update {
                        it.copy(
                            files = files.data,
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to fetch server files: ${error.message}")

                    onError("Failed to fetch server files: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun selectAllFiles() {
        _state.update {
            it.copy(
                selectedFiles = it.files.map { file -> file.attributes.name }
            )
        }
    }

    fun deselectAllFiles() {
        _state.update {
            it.copy(
                selectedFiles = emptyList()
            )
        }
    }

    fun toggleFileSelection(fileName: String) {
        _state.update {
            it.copy(
                selectedFiles = if (fileName in it.selectedFiles) {
                    it.selectedFiles - fileName
                } else {
                    it.selectedFiles + fileName
                }
            )
        }
    }

    fun navigateToDirectory(index: Int) {
        _state.update {
            it.copy(
                filesPath = it.filesPath.take(index + 1),
                selectedFiles = emptyList()
            )
        }
    }

    fun addDirectoryToPath(directory: String) {
        _state.update {
            it.copy(
                filesPath = it.filesPath + directory,
                selectedFiles = emptyList()
            )
        }
    }

    fun showCreateNewDirectoryPopup() {
        _state.update {
            it.copy(
                showNewDirectoryPopup = true
            )
        }
    }

    fun hideCreateNewDirectoryPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showNewDirectoryPopup = false,
                newDirectoryName = TextFieldValue("")
            )
        }
    }

    fun setNewDirectoryName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newDirectoryName = name
            )
        }
    }

    fun createNewDirectory(
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

            val newFolderRes = createServerFolder(
                context = context,
                serverId = serverId!!,
                name = _state.value.newDirectoryName.text.trim(),
                directory = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")
            )

            newFolderRes
                .onSuccess {
                    hideCreateNewDirectoryPopup(true)

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to create new directory: ${error.message}")

                    onError("Failed to create new directory: ${error.message}")

                    hideCreateNewDirectoryPopup(true)

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun showMoveFilesPopup(
        isRename: Boolean = false
    ) {
        _state.update {
            it.copy(
                showMoveFilesPopup = true,
                isRename = isRename
            )
        }
    }

    fun hideMoveFilesPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showMoveFilesPopup = false,
                newDirectoryName = TextFieldValue("")
            )
        }
    }

    fun moveFiles(
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

            val root = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            val selectedFiles = _state.value.selectedFiles

            val renameFiles = if (selectedFiles.size == 1) {
                listOf(
                    RenameServerFilesBody.File(
                        from = selectedFiles.first(),
                        to = _state.value.newDirectoryName.text.trim()
                    )
                )
            } else {
                selectedFiles.map { fileName ->
                    RenameServerFilesBody.File(
                        from = fileName,
                        to = "${_state.value.newDirectoryName.text.trim()}/$fileName"
                    )
                }
            }

            val moveFilesRes = renameServerFiles(
                context = context,
                serverId = serverId!!,
                directory = root,
                files = renameFiles
            )

            moveFilesRes
                .onSuccess {
                    hideMoveFilesPopup(true)

                    _state.update {
                        it.copy(
                            selectedFiles = emptyList()
                        )
                    }

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to move files: ${error.message}")

                    onError("Failed to move files: ${error.message}")

                    hideMoveFilesPopup(true)

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun showDeleteFilesPopup() {
        _state.update {
            it.copy(
                showDeleteFilesPopup = true
            )
        }
    }

    fun hideDeleteFilesPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showDeleteFilesPopup = false
            )
        }
    }

    fun deleteFiles(
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

            val root = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            val selectedFiles = _state.value.selectedFiles

            val deleteFilesRes = deleteServerFiles(
                context = context,
                serverId = serverId!!,
                directory = root,
                files = selectedFiles
            )

            deleteFilesRes
                .onSuccess {
                    hideDeleteFilesPopup(true)

                    _state.update {
                        it.copy(
                            selectedFiles = emptyList()
                        )
                    }

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to delete files: ${error.message}")

                    onError("Failed to delete files: ${error.message}")

                    hideDeleteFilesPopup(true)

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun archiveFiles(
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

            val root = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            val archiveFilesRes = compressServerFiles(
                context = context,
                serverId = serverId!!,
                directory = root,
                files = _state.value.selectedFiles
            )

            archiveFilesRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            selectedFiles = emptyList()
                        )
                    }

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to archive files: ${error.message}")

                    onError("Failed to archive files: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun unarchiveFile(
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

            val root = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            val unarchiveFilesRes = decompressServerFile(
                context = context,
                serverId = serverId!!,
                directory = root,
                file = _state.value.selectedFiles.first()
            )

            unarchiveFilesRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            selectedFiles = emptyList()
                        )
                    }

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to unarchive file: ${error.message}")

                    onError("Failed to unarchive file: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun copyFile(
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

            var root = (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            if (root.isBlank()) {
                root = "/"
            }

            val copyFilesRes = copyServerFile(
                context = context,
                serverId = serverId!!,
                file = root + _state.value.selectedFiles.first()
            )

            copyFilesRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            selectedFiles = emptyList()
                        )
                    }

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to copy file: ${error.message}")

                    onError("Failed to copy file: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun showUpdatePermissionsPopup() {
        _state.update {
            it.copy(
                showUpdatePermissionsPopup = true
            )
        }
    }

    fun hideUpdatePermissionsPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showUpdatePermissionsPopup = false,
                newPermissions = TextFieldValue("")
            )
        }
    }

    fun setNewPermissions(permissions: TextFieldValue) {
        _state.update {
            it.copy(
                newPermissions = permissions
            )
        }
    }

    fun updateFilePermissions(
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

            val root = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            val updatePermissionsRes = updateServerFilesPermissions(
                context = context,
                serverId = serverId!!,
                directory = root,
                files = listOf(
                    UpdateServerFilesPermissionsBody.File(
                        file = _state.value.selectedFiles.first(),
                        mode = _state.value.newPermissions.text.trim()
                    )
                )
            )

            updatePermissionsRes
                .onSuccess {
                    hideUpdatePermissionsPopup(true)

                    _state.update {
                        it.copy(
                            selectedFiles = emptyList()
                        )
                    }

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to copy file: ${error.message}")

                    onError("Failed to copy file: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    hideUpdatePermissionsPopup(true)
                }
        }
    }

    fun setSelectedUri(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(SecureStorage.STORAGE_FILE_DOWNLOAD_FOLDER_KEY, uri.toString())

            _state.update {
                it.copy(
                    selectedUri = uri
                )
            }

            selectedPath = getDisplayPath(uri)
        }
    }

    fun performDownload(
        context: Context,
        file: ServerFile,
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
                    fileSize = file.attributes.size
                )
            }

            if (!fileFits) {
                sendNotification {
                    Text(
                        text = "Not enough space in the selected directory to download the file",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                return@launch
            }

            val fileFitsCache = withContext(Dispatchers.IO) {
                StorageUtil.canFitInternalCache(
                    context = context,
                    fileSize = file.attributes.size
                )
            }

            if (!fileFitsCache) {
                sendNotification {
                    Text(
                        text = "Not enough space in the internal cache to download the file",
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

            val fileName = file.attributes.name

            val tempFile = java.io.File(context.cacheDir, fileName)
            val tempDestinationPath = tempFile.absolutePath

            if (tempFile.exists()) {
                withContext(Dispatchers.IO) {
                    tempFile.delete()
                }
            }

            var root = (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            if (root.isBlank()) {
                root = "/"
            }

            withContext(Dispatchers.IO) {
                val downloadRes = downloadServerFile(
                    context = context,
                    serverId = serverId!!,
                    file = root + file.attributes.name,
                    destinationPath = tempDestinationPath,
                    notificationTitle = "Downloading file",
                    notificationContent = "Downloading ${file.attributes.name}",
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
                                file.attributes.mimetype,
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
                                        text = "File downloaded to ${selectedPath}/$fileName",
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
                                "ClientServerFilesTabViewModel",
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
                        Logger.error("ClientServerFilesTabViewModel", "Failed to download file", it)

                        sendNotification {
                            Text(
                                text = "Failed to download file: ${it.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
            }
        }
    }

    fun uploadFiles(
        context: Context,
        files: List<UploadFile>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(isUploading = true)
            }

            val newFiles = files.mapNotNull { selectedFile ->
                val tempFile = withContext(Dispatchers.IO) {
                    copyUriToTempFile(
                        context = context,
                        uri = selectedFile.uri,
                        displayName = selectedFile.name
                    )
                }

                if (tempFile == null) {
                    return@mapNotNull null
                }

                return@mapNotNull selectedFile
            }

            val uploadRes = uploadServerFiles(
                context = context,
                serverId = serverId!!,
                directory = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/"),
                files = newFiles,
                notificationTitle = "Uploading files",
                notificationContent = "Uploading ${newFiles.size} files",
                onProgress = { total, transferred, _ ->
                    val totalFloat = total.toFloat()
                    val transferredFloat = transferred.toFloat()

                    val percent = if (totalFloat > 0f) ((transferredFloat * 100f) / total) else 0f

                    _state.update {
                        it.copy(
                            uploadPercent = percent
                        )
                    }
                },
            )

            uploadRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isUploading = false,
                            uploadPercent = 0f
                        )
                    }

                    updateFiles(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to upload files: ${error.message}")

                    onError("Failed to upload files: ${error.message}")

                    _state.update {
                        it.copy(
                            isUploading = false,
                            uploadPercent = 0f
                        )
                    }
                }
        }
    }

    fun setFileContent(context: TextFieldValue) {
        _state.update {
            it.copy(
                fileContent = context
            )
        }
    }

    fun setSelectedLanguage(language: HighlightLanguage) {
        _state.update {
            it.copy(
                selectedLanguage = language
            )
        }
    }

    fun parseFileLanguage(file: ServerFile) {
        val fileName = file.attributes.name

        val languageFromExtension = HighlightLanguage.fromExtension(fileName)

        if (languageFromExtension is HighlightLanguage) {
            setSelectedLanguage(languageFromExtension)

            return
        }

        val languageFromMimetype = HighlightLanguage.fromMimeType(file.attributes.mimetype)

        if (languageFromMimetype is HighlightLanguage) {
            setSelectedLanguage(languageFromMimetype)

            return
        }

        setSelectedLanguage(HighlightLanguage.PLAIN_TEXT)
    }

    fun showUnsavedFileWarningPopup() {
        _state.update {
            it.copy(
                showUnsavedFileWarningPopup = true
            )
        }
    }

    fun hideUnsavedFileWarningPopup() {
        _state.update {
            it.copy(
                showUnsavedFileWarningPopup = false
            )
        }
    }

    fun setCreateNewFile(createNewFile: Boolean) {
        _state.update {
            it.copy(
                createNewFile = createNewFile
            )
        }
    }

    fun showNewFileNamePopup() {
        _state.update {
            it.copy(
                showNewFileNamePopup = true
            )
        }
    }

    fun hideNewFileNamePopup(skipFileSaving: Boolean = false) {
        if (_state.value.isFileSaving && !skipFileSaving) return

        _state.update {
            it.copy(
                showNewFileNamePopup = false,
                newFileName = TextFieldValue("")
            )
        }
    }

    fun setNewFileName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newFileName = name
            )
        }
    }

    fun setFileToEdit(
        context: Context,
        file: ServerFile,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            parseFileLanguage(file)

            var root = (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

            if (root.isBlank()) {
                root = "/"
            }

            val filePath = root + file.attributes.name

            _state.update {
                it.copy(
                    isFetchingFileContent = true,
                    fileToEdit = filePath
                )
            }

            val fileContentRes = getServerFileContents(
                context = context,
                serverId = serverId!!,
                file = filePath
            )

            fileContentRes
                .onSuccess { content ->
                    _state.update {
                        it.copy(
                            isFetchingFileContent = false,
                            fileContent = TextFieldValue(content),
                            originalFileContent = content
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to fetch file content: ${error.message}")

                    onError("Failed to fetch file content: ${error.message}")

                    _state.update {
                        it.copy(
                            isFetchingFileContent = false,
                            fileToEdit = null
                        )
                    }
                }
        }
    }

    fun clearFileToEdit() {
        _state.update {
            it.copy(
                fileToEdit = null,
            )
        }
    }

    fun setOriginalFileContent(content: String) {
        _state.update {
            it.copy(
                originalFileContent = content
            )
        }
    }

    fun saveFile(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isFileSaving = true
                )
            }

            if (_state.value.createNewFile || _state.value.fileToEdit == null) {
                val languageByExtension = HighlightLanguage.fromExtension(_state.value.newFileName.text.trim())

                setSelectedLanguage(languageByExtension ?: HighlightLanguage.PLAIN_TEXT)
            }

            val writeFileRes = writeServerFile(
                context = context,
                serverId = serverId!!,
                file = if (_state.value.createNewFile || _state.value.fileToEdit == null) {
                    var root = (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/")

                    if (root.isBlank()) {
                        root = "/"
                    }

                    root + _state.value.newFileName.text.trim()
                } else {
                    _state.value.fileToEdit!!
                },
                content = _state.value.fileContent.text.trim()
            )

            writeFileRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isFileSaving = false,
                            originalFileContent = _state.value.fileContent.text.trim(),
                            fileContent = TextFieldValue(_state.value.fileContent.text.trim())
                        )
                    }

                    hideNewFileNamePopup(true)

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error("ClientServerFilesTabViewModel", "Failed to save file: ${error.message}")

                    _state.update {
                        it.copy(
                            isFileSaving = false
                        )
                    }

                    onError("Failed to save file: ${error.message}")
                }
        }
    }
}