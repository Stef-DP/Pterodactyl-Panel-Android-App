package com.stefdp.pterodactylpanel.screens.client.server.tabs.console

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.client.models.ServerState
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.getServerWebsocket
import com.stefdp.pterodactylpanel.network.websocket.WebSocket
import com.stefdp.pterodactylpanel.network.websocket.WebSocketManager
import com.stefdp.pterodactylpanel.network.websocket.models.WSEvents
import com.stefdp.pterodactylpanel.network.websocket.models.responses.WebSocketStats
import com.stefdp.pterodactylpanel.screens.client.server.WebSocketConnectionStatus
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.utils.formatMs
import ir.ehsannarmani.compose_charts.models.Line
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import java.util.Locale

data class ClientServerConsoleTabUiState(
    val server: GetServerResponse? = null,
    val isServerOwner: Boolean = false,
    val userPermissions: List<ServerSubuser.Permissions> = emptyList(),
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
)

private const val MAX_LOGS = 250

class ClientServerConsoleTabViewModel(
    private val wsManager: WebSocketManager = WebSocket.wsManager
) : ViewModel() {
    private val _state: MutableStateFlow<ClientServerConsoleTabUiState> = MutableStateFlow(ClientServerConsoleTabUiState())
    val state: StateFlow<ClientServerConsoleTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    private var webSocketObservationJob: Job? = null

    private var cpuLoadLineData: List<Double> = (1..20).map { 0.0 }
    private var memoryLineData: List<Double> = (1..20).map { 0.0 }
    private var networkInboundLineData: List<Double> = (1..20).map { 0.0 }
    private var networkOutboundLineData: List<Double> = (1..20).map { 0.0 }

    private var lastRxBytes: Long? = null
    private var lastTxBytes: Long? = null
    private var lastStatsTimestamp: Long = 0L

    fun init(server: GetServerResponse?) {
        serverId = server?.attributes?.identifier

        _state.update {
            it.copy(
                server = server,
                isServerOwner = server?.meta?.isServerOwner ?: false,
                userPermissions = server?.meta?.userPermissions ?: emptyList()
            )
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