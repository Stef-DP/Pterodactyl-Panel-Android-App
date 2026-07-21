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
import com.stefdp.pterodactylpanel.network.client.models.ServerDatabase
import com.stefdp.pterodactylpanel.network.client.models.ServerFile
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.client.models.ServerState
import com.stefdp.pterodactylpanel.network.client.models.requests.ListServerDatabasesQueryInclude
import com.stefdp.pterodactylpanel.network.client.models.requests.RenameServerFile
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerFilePermissionsFile
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.UploadFile
import com.stefdp.pterodactylpanel.network.client.requests.compressServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.copyServerFile
import com.stefdp.pterodactylpanel.network.client.requests.createServerDatabase
import com.stefdp.pterodactylpanel.network.client.requests.createServerFolder
import com.stefdp.pterodactylpanel.network.client.requests.decompressServerFile
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerDatabase
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.downloadServerFile
import com.stefdp.pterodactylpanel.network.client.requests.getServer
import com.stefdp.pterodactylpanel.network.client.requests.getServerFileContents
import com.stefdp.pterodactylpanel.network.client.requests.getServerWebsocket
import com.stefdp.pterodactylpanel.network.client.requests.listServerDatabases
import com.stefdp.pterodactylpanel.network.client.requests.listServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.renameServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.rotateServerDatabasePassword
import com.stefdp.pterodactylpanel.network.client.requests.updateServerFilesPermissions
import com.stefdp.pterodactylpanel.network.client.requests.uploadServerFiles
import com.stefdp.pterodactylpanel.network.client.requests.writeServerFile
import com.stefdp.pterodactylpanel.network.websocket.WebSocket
import com.stefdp.pterodactylpanel.network.websocket.WebSocketManager
import com.stefdp.pterodactylpanel.network.websocket.models.WSEvents
import com.stefdp.pterodactylpanel.network.websocket.models.responses.WebSocketStats
import com.stefdp.pterodactylpanel.ui.theme.HighlightLanguage
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.utils.StorageUtil
import com.stefdp.pterodactylpanel.utils.copyUriToTempFile
import com.stefdp.pterodactylpanel.utils.formatMs
import com.stefdp.pterodactylpanel.utils.getDisplayPath
import ir.ehsannarmani.compose_charts.models.Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    val server: GetServerResponse? = null,
    val connectionState: WebSocketConnectionStatus = WebSocketConnectionStatus.DISCONNECTED,
    val logs: List<String> = emptyList(),
    val status: ServerState = ServerState.OFFLINE,
    val cpuUsage: String = "0.00%",
    val memoryUsage: String = "0 Bytes",
    val diskUsage: String = "0 Bytes",
    val incomingNetwork: String = "0 Bytes",
    val outgoingNetwork: String = "0 Bytes",
    val uptime: String = "Offline",
    val commandToSend: TextFieldState = TextFieldState(""),
    val currentTab: ServerTab = ServerTab.DATABASES, // TODO: set this back to CONSOLE
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
    val databases: List<ServerDatabase> = emptyList(),
    val databaseToDelete: String? = null,
    val databaseToShowDetails: String? = null,
    val confirmDatabaseNameValue: TextFieldValue = TextFieldValue(""),
    val showCreateDatabasePopup: Boolean = false,
    val newDatabaseName: TextFieldValue = TextFieldValue(""),
    val newDatabaseAllowedIp: TextFieldValue = TextFieldValue(""),
)

private const val MAX_LOGS = 250

class ClientServerViewModel(
    private val wsManager: WebSocketManager = WebSocket.wsManager
) : ViewModel() {
    private val _state: MutableStateFlow<ClientServerUiState> = MutableStateFlow(ClientServerUiState())
    val state: StateFlow<ClientServerUiState> = _state.asStateFlow()

    private var serverId: String? = null

    private var webSocketObservationJob: Job? = null

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
                    _state.update {
                        it.copy(
                            server = server
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
        if (tab != ServerTab.CONSOLE) {
            _state.update {
                it.copy(
                    logs = emptyList(),
//                    connectionState = WebSocketConnectionStatus.DISCONNECTED,
//                    status = ServerState.OFFLINE,
//                    cpuUsage = "0.00%",
//                    memoryUsage = "0 Bytes",
//                    diskUsage = "0 Bytes",
//                    incomingNetwork = "0 Bytes",
//                    outgoingNetwork = "0 Bytes",
//                    uptime = "Offline",
//                    cpuLoadLineChartLines = listOf(
//                        Line(
//                            values = emptyList(),
//                            color = SolidColor(Color.Transparent),
//                            firstGradientFillColor = Color.Transparent,
//                            secondGradientFillColor = Color.Transparent,
//                        )
//                    ),
//                    memoryLineChartLines = listOf(
//                        Line(
//                            values = emptyList(),
//                            color = SolidColor(Color.Transparent),
//                            firstGradientFillColor = Color.Transparent,
//                            secondGradientFillColor = Color.Transparent,
//                        )
//                    ),
//                    networkLineChartLines = listOf(
//                        Line(
//                            values = emptyList(),
//                            color = SolidColor(Color.Transparent),
//                            firstGradientFillColor = Color.Transparent,
//                            secondGradientFillColor = Color.Transparent,
//                        ),
//                        Line(
//                            values = emptyList(),
//                            color = SolidColor(Color.Transparent),
//                            firstGradientFillColor = Color.Transparent,
//                            secondGradientFillColor = Color.Transparent,
//                        )
//                    ),
                )
            }

//            lastRxBytes = null
//            lastTxBytes = null
//            lastStatsTimestamp = 0L
        }

//        if (tab != ServerTab.FILES) {
//            _state.update {
//                it.copy(
//                    selectedFiles = emptyList(),
//                    filesPath = listOf(
//                        "home",
//                        "container",
//                    ),
//                    files = emptyList(),
//                    createNewFile = false,
//                    fileToEdit = null,
//                    fileContent = TextFieldValue(""),
//                    originalFileContent = "",
//                    selectedLanguage = HighlightLanguage.PLAIN_TEXT,
//                )
//            }
//        }

//        if (tab != ServerTab.DATABASES) {
//            _state.update {
//                it.copy(
//                    databases = emptyList()
//                )
//            }
//        }

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
                        to = _state.value.newDirectoryName.text.trim()
                    )
                )
            } else {
                selectedFiles.map { fileName ->
                    RenameServerFile(
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
                    Logger.error("ClientServerViewModel", "Failed to fetch file content: ${error.message}")

                    onError("Failed to fetch file content")

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
                .onFailure {
                    Logger.error("ClientServerViewModel", "Failed to save file: ${it.message}")

                    _state.update {
                        it.copy(
                            isFileSaving = false
                        )
                    }

                    onError("Failed to save file")
                }
        }
    }

    fun updateDatabases(
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
                    isLoading = true
                )
            }

            val serverDatabasesRes = listServerDatabases(
                context = context,
                serverId = serverId!!,
                include = ListServerDatabasesQueryInclude.PASSWORD.toString()
            )

            serverDatabasesRes
                .onSuccess { databases ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            databases = databases.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error("ClientServerViewModel", "Failed to fetch server databases: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server databases")
                }
        }
    }

    fun setDatabaseToDelete(
        database: String?,
        skipLoading: Boolean = false
    ) {
        if (database == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                databaseToDelete = database,
                confirmDatabaseNameValue = TextFieldValue("")
            )
        }
    }

    fun deleteDatabase(
        context: Context,
        databaseId: String,
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

            val deleteDatabaseRes = deleteServerDatabase(
                context = context,
                serverId = serverId!!,
                databaseId = databaseId
            )

            deleteDatabaseRes
                .onSuccess {
                    setDatabaseToDelete(null, true)

                    updateDatabases(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerViewModel", "Failed to delete database: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete database")
                }
        }
    }

    fun setDatabaseToShowDetails(database: String?) {
        _state.update {
            it.copy(
                databaseToShowDetails = database
            )
        }
    }

    fun rotateDatabasePassword(
        context: Context,
        databaseId: String,
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

            val rotatePasswordRes = rotateServerDatabasePassword(
                context = context,
                serverId = serverId!!,
                databaseId = databaseId
            )

            rotatePasswordRes
                .onSuccess { database ->
                    val databaseIndex = _state.value.databases.indexOfFirst { it.attributes.id == database.attributes.id }

                    if (databaseIndex == -1) {
                        updateDatabases(
                            context = context,
                            onError = onError,
                            onSuccess = onSuccess
                        )
                    } else {
                        val updatedDatabases = _state.value.databases.toMutableList()
                        updatedDatabases[databaseIndex] = database

                        _state.update {
                            it.copy(
                                isLoading = false,
                                databases = updatedDatabases.toList()
                            )
                        }

                        onSuccess()
                    }
                }
                .onFailure { error ->
                    Logger.error("ClientServerViewModel", "Failed to rotate database password: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to rotate database password")
                }
        }
    }

    fun setConfirmDeleteDatabaseNameValue(name: TextFieldValue) {
        _state.update {
            it.copy(
                confirmDatabaseNameValue = name
            )
        }
    }

    fun showCreateDatabasePopup() {
        _state.update {
            it.copy(
                showCreateDatabasePopup = true,
            )
        }
    }

    fun hideCreateDatabasePopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateDatabasePopup = false,
                newDatabaseName = TextFieldValue(""),
                newDatabaseAllowedIp = TextFieldValue(""),
            )
        }
    }

    fun createDatabase(
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

            val allowedIp = if (_state.value.newDatabaseAllowedIp.text.trim().isBlank()) {
                "%"
            } else {
                _state.value.newDatabaseAllowedIp.text.trim()
            }

            val createDatabaseRes = createServerDatabase(
                context = context,
                serverId = serverId!!,
                databaseName = _state.value.newDatabaseName.text.trim(),
                allowedIp = allowedIp
            )

            createDatabaseRes
                .onSuccess {
                    hideCreateDatabasePopup(true)

                    updateDatabases(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.error("ClientServerViewModel", "Failed to create database: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create database: ${error.message}")
                }
        }
    }

    fun setNewDatabaseName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newDatabaseName = name
            )
        }
    }

    fun setNewDatabaseAllowedIp(ip: TextFieldValue) {
        _state.update {
            it.copy(
                newDatabaseAllowedIp = ip
            )
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
        webSocketObservationJob?.cancel()

        webSocketObservationJob = viewModelScope.launch {
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

    fun disconnectFromWebSocket() {
        webSocketObservationJob?.cancel()
        webSocketObservationJob = null

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