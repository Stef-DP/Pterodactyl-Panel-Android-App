package com.stefdp.pterodactylpanel.screens.client.server

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerFile
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.client.models.ServerState
import com.stefdp.pterodactylpanel.network.client.models.requests.RenameServerFile
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerFilePermissionsFile
import com.stefdp.pterodactylpanel.network.client.requests.UploadFile
import com.stefdp.pterodactylpanel.network.client.requests.compressServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.copyServerFile
import com.stefdp.pterodactylpanel.network.client.requests.createServerFolder
import com.stefdp.pterodactylpanel.network.client.requests.decompressServerFile
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.downloadServerFile
import com.stefdp.pterodactylpanel.network.client.requests.getServer
import com.stefdp.pterodactylpanel.network.client.requests.getServerWebsocket
import com.stefdp.pterodactylpanel.network.client.requests.listServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.renameServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.updateServerFilesPermissions
import com.stefdp.pterodactylpanel.network.client.requests.uploadServerFiles
import com.stefdp.pterodactylpanel.network.websocket.WebSocket
import com.stefdp.pterodactylpanel.network.websocket.WebSocketManager
import com.stefdp.pterodactylpanel.network.websocket.models.WSEvents
import com.stefdp.pterodactylpanel.network.websocket.models.responses.WebSocketStats
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.utils.StorageUtil
import com.stefdp.pterodactylpanel.utils.copyUriToTempFile
import com.stefdp.pterodactylpanel.utils.formatMs
import com.stefdp.pterodactylpanel.utils.getDisplayPath
import ir.ehsannarmani.compose_charts.models.Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.jacobras.humanreadable.HumanReadable
import java.util.Locale

data class ClientServerUiState(
    val isLoading: Boolean = true,
    val connectionState: WebSocketConnectionStatus = WebSocketConnectionStatus.DISCONNECTED,
    val logs: List<String> = emptyList(),
    val status: ServerState = ServerState.OFFLINE,
    val cpuUsage: String = "0.00%",
    val memoryUsage: String = "0 Bytes",
    val diskUsage: String = "0 Bytes",
    val incomingNetwork: String = "0 Bytes",
    val outgoingNetwork: String = "0 Bytes",
    val address: String = "Unknown",
    val uptime: String = "Offline",
    val name: String = "Unknown",
    val commandToSend: TextFieldState = TextFieldState(""),
    val cpuLimit: String = "Unlimited",
    val memoryLimit: String = "Unlimited",
    val diskLimit: String = "Unlimited",
    val currentTab: ServerTab = ServerTab.FILES, // TODO: set this back to CONSOLE
    val cpuLoadLineChartLines: List<Line> = listOf(
        Line(
            values = emptyList(),
            color = SolidColor(Color.Transparent),
            firstGradientFillColor = Color.Transparent,
            secondGradientFillColor = Color.Transparent,
        )
    ),
    val memoryLineChartLines: List<Line> = listOf(
        Line(
            values = emptyList(),
            color = SolidColor(Color.Transparent),
            firstGradientFillColor = Color.Transparent,
            secondGradientFillColor = Color.Transparent,
        )
    ),
    val networkLineChartLines: List<Line> = listOf(
        Line(
            values = emptyList(),
            color = SolidColor(Color.Transparent),
            firstGradientFillColor = Color.Transparent,
            secondGradientFillColor = Color.Transparent,
        ),
        Line(
            values = emptyList(),
            color = SolidColor(Color.Transparent),
            firstGradientFillColor = Color.Transparent,
            secondGradientFillColor = Color.Transparent,
        )
    ),
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
    val uploadPercent: Float = 0f
)

private const val MAX_LOGS = 250

class ClientServerViewModel(
    private val wsManager: WebSocketManager = WebSocket.wsManager
) : ViewModel() {
    private val _state: MutableStateFlow<ClientServerUiState> = MutableStateFlow(ClientServerUiState())
    val state: StateFlow<ClientServerUiState> = _state.asStateFlow()

    private var serverId: String? = null

    private var cpuLoadLineData: List<Double> = (1..20).map { 0.0 }
    private var memoryLineData: List<Double> = (1..20).map { 0.0 }
    private var networkInboundLineData: List<Double> = (1..20).map { 0.0 }
    private var networkOutboundLineData: List<Double> = (1..20).map { 0.0 }

    private var lastRxBytes: Long? = null
    private var lastTxBytes: Long? = null
    private var lastStatsTimestamp: Long = 0L

    private var selectedPath: String? = null

    fun init(
        context: Context,
        serverId: String,
        directory: String?,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            this@ClientServerViewModel.serverId = serverId

            val secureStore = SecureStorage.getInstance(context)

            val fileDownloadFolder = secureStore.get(SecureStorage.STORAGE_FILE_DOWNLOAD_FOLDER_KEY)

            val fileDownloadFolderUri = fileDownloadFolder?.toUri()

            _state.update {
                it.copy(
                    selectedUri = fileDownloadFolderUri
                )
            }

            selectedPath = fileDownloadFolderUri?.let { uri -> getDisplayPath(uri) }

            if (directory != null) {
                _state.update {
                    it.copy(
                        currentTab = ServerTab.FILES,
                        filesPath = listOf(
                            "home",
                            "container",
                        ) + directory.split("/").filter { it.isNotBlank() }
                    )
                }
            }

            val serverRes = getServer(
                context = context,
                serverId = serverId
            )

            serverRes
                .onSuccess { server ->
                    val defaultAllocation = server.attributes.relationships.allocations.data.find { it.attributes.isDefault }?.attributes

                    val address = if (defaultAllocation != null) {
                        "${defaultAllocation.ipAlias ?: defaultAllocation.ip}:${defaultAllocation.port}"
                    } else {
                        "Unknown"
                    }

                    _state.update {
                        it.copy(
                            address = address,
                            name = server.attributes.name,
                            cpuLimit = if (server.attributes.limits.cpu == 0L) "∞" else "${server.attributes.limits.cpu}%",
                            memoryLimit = if (server.attributes.limits.memory == 0L) "∞" else {
                                HumanReadable.fileSize(
                                    bytes = server.attributes.limits.memory * 1024L * 1024L,
                                    decimals = 2
                                )
                            },
                            diskLimit = if (server.attributes.limits.disk == 0L) "∞" else {
                                HumanReadable.fileSize(
                                    bytes = server.attributes.limits.disk * 1024L * 1024L,
                                    decimals = 2
                                )
                            }
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error("ClientServerViewModel", "Failed to fetch server data: ${error.message}")

                    onError("Failed to fetch server data")
                }

            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun setCurrentTab(tab: ServerTab) {
        if (tab != ServerTab.CONSOLE && _state.value.connectionState == WebSocketConnectionStatus.CONNECTED) {
            disconnectFromWebSocket()
        }

        if (tab != ServerTab.FILES) {
            _state.update {
                it.copy(
                    selectedFiles = emptyList(),
                    filesPath = listOf(
                        "home",
                        "container",
                    ),
                    files = emptyList()
                )
            }
        }

        _state.update {
            it.copy(currentTab = tab)
        }
    }

    fun sendCommand() {
        val command = _state.value.commandToSend.text.trim()

        if (command.isNotEmpty()) {
            sendCommand(command.toString())

            _state.update {
                it.copy(commandToSend = TextFieldState(""))
            }
        }
    }

    fun updateCharts(
        primaryColor: Color,
        secondaryColor: Color
    ) {
        _state.update { current ->
            current.copy(
                cpuLoadLineChartLines = current.cpuLoadLineChartLines.map { line ->
                    line.copy(
                        color = SolidColor(primaryColor),
                        firstGradientFillColor = primaryColor.copy(alpha = 0.5f),
                        secondGradientFillColor = primaryColor.copy(alpha = 0.1f)
                    )
                },
                memoryLineChartLines = current.memoryLineChartLines.map { line ->
                    line.copy(
                        color = SolidColor(primaryColor),
                        firstGradientFillColor = primaryColor.copy(alpha = 0.5f),
                        secondGradientFillColor = primaryColor.copy(alpha = 0.1f)
                    )
                },
                networkLineChartLines = current.networkLineChartLines.mapIndexed { index, line ->
                    val color = if (index == 0) primaryColor else secondaryColor

                    line.copy(
                        color = SolidColor(color),
                        firstGradientFillColor = color.copy(alpha = 0.5f),
                        secondGradientFillColor = color.copy(alpha = 0.1f)
                    )
                }
            )
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
                    Logger.error("ClientServerViewModel", "Failed to fetch server files: ${error.message}")

                    onError("Failed to fetch server files")

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
                    Logger.error("ClientServerViewModel", "Failed to create new directory: ${error.message}")

                    onError("Failed to create new directory")

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
                    RenameServerFile(
                        from = selectedFiles.first(),
                        to = _state.value.newDirectoryName.text
                    )
                )
            } else {
                selectedFiles.map { fileName ->
                    RenameServerFile(
                        from = fileName,
                        to = "${_state.value.newDirectoryName.text}/$fileName"
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
                    Logger.error("ClientServerViewModel", "Failed to move files: ${error.message}")

                    onError("Failed to move files")

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
                    Logger.error("ClientServerViewModel", "Failed to delete files: ${error.message}")

                    onError("Failed to delete files")

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
                    Logger.error("ClientServerViewModel", "Failed to archive files: ${error.message}")

                    onError("Failed to archive files")

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
                    Logger.error("ClientServerViewModel", "Failed to unarchive file: ${error.message}")

                    onError("Failed to unarchive file")

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

            val root = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/") + "/"

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
                    Logger.error("ClientServerViewModel", "Failed to copy file: ${error.message}")

                    onError("Failed to copy file")

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
                    UpdateServerFilePermissionsFile(
                        file = _state.value.selectedFiles.first(),
                        mode = _state.value.newPermissions.text
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
                    Logger.error("ClientServerViewModel", "Failed to copy file: ${error.message}")

                    onError("Failed to copy file")

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

            val root = "/" + (_state.value.filesPath - _state.value.filesPath.take(2).toSet()).joinToString("/") + "/"

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
                                "ClientServerViewModel",
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
                        Logger.error("ClientServerViewModel", "Failed to download file", it)

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
                onProgress = { total, transferred, speed ->
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
                    Logger.error("ClientServerViewModel", "Failed to upload files: ${error.message}")

                    onError("Failed to upload files")

                    _state.update {
                        it.copy(
                            isUploading = false,
                            uploadPercent = 0f
                        )
                    }
                }
        }
    }

    fun connectToWebSocket(
        context: Context,
        locale: Locale,
        onError: (String) -> Unit
    ) {
        if (serverId == null) {
            onError("Missing server ID")

            return
        }

        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(connectionState = WebSocketConnectionStatus.CONNECTING)
                }

                val secureStore = SecureStorage.getInstance(context)

                val panelUrl = secureStore.get(SecureStorage.STORAGE_SERVER_URL_KEY)

                if (panelUrl == null) {
                    _state.update {
                        it.copy(connectionState = WebSocketConnectionStatus.DISCONNECTED)
                    }

                    onError("Missing server URL")

                    return@launch
                }

                val serverSocketRes = getServerWebsocket(
                    context = context,
                    serverId = serverId!!
                )

                serverSocketRes
                    .onSuccess { res ->
                        wsManager.connect(
                            wsUrl = res.data.socket,
                            initialToken = res.data.token,
                            origin = panelUrl
                        )

                        wsManager.onTokenRequired = {
                            refreshToken(
                                context = context,
                                onError = onError
                            )
                        }

                        observeWebSocket(locale)

                        _state.update {
                            it.copy(connectionState = WebSocketConnectionStatus.CONNECTED)
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(connectionState = WebSocketConnectionStatus.DISCONNECTED)
                        }

                        onError("Failed to connect to console")
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(connectionState = WebSocketConnectionStatus.DISCONNECTED)
                }

                onError("Failed to connect to the console")
            }
        }
    }

    private fun observeWebSocket(locale: Locale) {
        viewModelScope.launch {
            wsManager.events.collect { message ->
                val firstArg = message.args?.firstOrNull()

                Logger.debug("WebSocketEvent", "Received event: ${message.event}, args: ${message.args}")

                when (message.event) {
                    WSEvents.AUTH_SUCCESS -> {
                        lastRxBytes = null
                        lastTxBytes = null
                        lastStatsTimestamp = System.currentTimeMillis()

                        wsManager.requestLogs()
                        wsManager.requestStats()
                    }

                    WSEvents.CONSOLE_OUTPUT -> {
                        val newLog = firstArg?.asString ?: ""

                        _state.update {
                            it.copy(
                                logs = (it.logs + newLog).takeLast(MAX_LOGS)
                            )
                        }
                    }

                    WSEvents.STATUS -> {
                        val currentStatus = ServerState.entries.find {
                            it.value.equals(firstArg?.asString, ignoreCase = true)
                        } ?: ServerState.OFFLINE

                        val yellowAnsi = "\u001B[33m\u001B[1m"
                        val resetAnsi = "\u001B[39m"

                        val statusMessages = mapOf(
                            ServerState.OFFLINE to "${yellowAnsi}container@pterodactyl~$resetAnsi Server marked as offline...",
                            ServerState.STOPPING to "${yellowAnsi}container@pterodactyl~$resetAnsi Server marked as stopping...",
                            ServerState.INSTALLING to "${yellowAnsi}container@pterodactyl~$resetAnsi Server marked as installing...",
                            ServerState.SUSPENDED to "${yellowAnsi}container@pterodactyl~$resetAnsi Server marked as suspended...",
                            ServerState.STARTING to "${yellowAnsi}container@pterodactyl~$resetAnsi Server marked as starting...",
                            ServerState.RUNNING to "${yellowAnsi}container@pterodactyl~$resetAnsi Server marked as running...",
                        )

                        val message = statusMessages[currentStatus]

                        _state.update {
                            it.copy(
                                status = currentStatus,
                                logs = if (message != null) {
                                    (it.logs + message).takeLast(MAX_LOGS)
                                } else {
                                    it.logs
                                }
                            )
                        }
                    }

                    WSEvents.STATS -> {
                        val statsJson = firstArg?.asString ?: ""

                        val stats = com.google.gson.Gson().fromJson(statsJson, WebSocketStats::class.java)

                        val serverStatus = _state.value.status

                        val currentTime = System.currentTimeMillis()
                        val timeDeltaSeconds = (
                            (currentTime - lastStatsTimestamp) / 1000.0
                        ).coerceAtLeast(0.1)

                        lastStatsTimestamp = currentTime

                        val currentInboundSpeed = if (lastRxBytes != null && stats.network.rxBytes >= lastRxBytes!!) {
                            (
                                (stats.network.rxBytes - lastRxBytes!!) / timeDeltaSeconds
                            ).coerceAtLeast(0.0)
                        } else {
                            0.0
                        }
                        lastRxBytes = stats.network.rxBytes

                        val currentOutboundSpeed = if (lastTxBytes != null && stats.network.txBytes >= lastTxBytes!!) {
                            (
                                (stats.network.txBytes - lastTxBytes!!) / timeDeltaSeconds
                            ).coerceAtLeast(0.0)
                        } else {
                            0.0
                        }
                        lastTxBytes = stats.network.txBytes

                        val newCpuLoadLineData = (cpuLoadLineData + stats.cpuAbsolute).takeLast(20)
                        val newMemoryLineData = (memoryLineData + stats.memoryBytes).takeLast(20).map { it.toDouble() }
                        val newNetworkInboundLineData = (networkInboundLineData + currentInboundSpeed).takeLast(20)
                        val newNetworkOutboundLineData = (networkOutboundLineData + currentOutboundSpeed).takeLast(20)

                        cpuLoadLineData = newCpuLoadLineData
                        memoryLineData = newMemoryLineData
                        networkInboundLineData = newNetworkInboundLineData
                        networkOutboundLineData = newNetworkOutboundLineData

                        _state.update {
                            it.copy(
                                cpuUsage = String.format(
                                    locale,
                                    "%.2f%%",
                                    stats.cpuAbsolute
                                ),
                                memoryUsage = HumanReadable.fileSize(
                                    bytes = stats.memoryBytes,
                                    decimals = 2
                                ),
                                diskUsage = HumanReadable.fileSize(
                                    bytes = stats.diskBytes,
                                    decimals = 2
                                ),
                                incomingNetwork = HumanReadable.fileSize(
                                    bytes = stats.network.rxBytes,
                                    decimals = 2
                                ),
                                outgoingNetwork = HumanReadable.fileSize(
                                    bytes = stats.network.txBytes,
                                    decimals = 2
                                ),
                                uptime = when (serverStatus) {
                                    ServerState.OFFLINE -> {
                                        "Offline"
                                    }
                                    else -> formatMs(
                                        ms = stats.uptime.toDouble(),
                                        abbreviated = true,
                                        limit = 3
                                    )
                                },
                                cpuLoadLineChartLines = it.cpuLoadLineChartLines.map { line ->
                                    line.copy(
                                        values = newCpuLoadLineData
                                    )
                                },
                                memoryLineChartLines = it.memoryLineChartLines.map { line ->
                                    line.copy(
                                        values = newMemoryLineData
                                    )
                                },
                                networkLineChartLines = it.networkLineChartLines.mapIndexed { index, line ->
                                    when (index) {
                                        0 -> line.copy(
                                            values = newNetworkOutboundLineData
                                        )
                                        1 -> line.copy(
                                            values = newNetworkInboundLineData
                                        )
                                        else -> line
                                    }
                                }
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun refreshToken(
        context: Context,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val serverSocketRes = getServerWebsocket(
                    context = context,
                    serverId = serverId!!
                )

                serverSocketRes
                    .onSuccess { res ->
                        wsManager.authenticate(res.data.token)
                    }
                    .onFailure { error ->
                        onError("Failed to refresh console token")

                        disconnectFromWebSocket()
                    }
            } catch (e: Exception) {
                onError("Failed to refresh console token")

                wsManager.disconnect()

                _state.update {
                    it.copy(connectionState = WebSocketConnectionStatus.DISCONNECTED)
                }
            }
        }
    }

    private fun disconnectFromWebSocket() {
        wsManager.disconnect()

        _state.update {
            it.copy(connectionState = WebSocketConnectionStatus.DISCONNECTED)
        }
    }

    fun sendCommand(command: String) {
        wsManager.sendCommand(command)
    }

    fun sendPowerSignal(action: ServerPowerSignal) {
        wsManager.sendPowerSignal(action)
    }

    override fun onCleared() {
        disconnectFromWebSocket()
    }
}