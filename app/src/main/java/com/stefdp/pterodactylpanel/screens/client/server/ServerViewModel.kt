package com.stefdp.pterodactylpanel.screens.client.server

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.client.models.ServerState
import com.stefdp.pterodactylpanel.network.client.requests.getServer
import com.stefdp.pterodactylpanel.network.client.requests.getServerWebsocket
import com.stefdp.pterodactylpanel.network.websocket.WebSocket
import com.stefdp.pterodactylpanel.network.websocket.WebSocketManager
import com.stefdp.pterodactylpanel.network.websocket.models.WSEvents
import com.stefdp.pterodactylpanel.network.websocket.models.responses.WebSocketStats
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.utils.formatMs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import java.util.Locale

data class ClientServerUiState(
    val isLoading: Boolean = true,
    val serverId: String? = null,
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
    val commandToSend: TextFieldState = TextFieldState(""),
    val cpuLimit: String = "Unlimited",
    val memoryLimit: String = "Unlimited",
    val diskLimit: String = "Unlimited"
)

private const val MAX_LOGS = 250

class ClientServerViewModel(
    private val wsManager: WebSocketManager = WebSocket.wsManager
) : ViewModel() {
    private val _state: MutableStateFlow<ClientServerUiState> = MutableStateFlow(ClientServerUiState())
    val state: StateFlow<ClientServerUiState> = _state.asStateFlow()

    fun init(
        context: Context,
        serverId: String,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(serverId = serverId)
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

    fun sendCommand() {
        val command = _state.value.commandToSend.text.trim()

        if (command.isNotEmpty()) {
            sendCommand(command.toString())

            _state.update {
                it.copy(commandToSend = TextFieldState(""))
            }
        }
    }

    fun connectToWebSocket(
        context: Context,
        locale: Locale,
        onError: (String) -> Unit
    ) {
        if (_state.value.serverId == null) {
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
                    serverId = _state.value.serverId!!
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

                        onError("Failed to connect to console: ${error.message}")
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
                    serverId = _state.value.serverId!!
                )

                serverSocketRes
                    .onSuccess { res ->
                        wsManager.authenticate(res.data.token)
                    }
                    .onFailure { error ->
                        onError("Failed to refresh console token: ${error.message}")

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

    fun appendStatusLog(log: String) {

    }

    override fun onCleared() {
        disconnectFromWebSocket()
    }
}