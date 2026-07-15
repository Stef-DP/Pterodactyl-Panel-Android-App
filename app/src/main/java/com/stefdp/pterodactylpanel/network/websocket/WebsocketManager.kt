package com.stefdp.pterodactylpanel.network.websocket

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.websocket.models.WSEvents
import com.stefdp.pterodactylpanel.network.websocket.models.WSMessage
import com.stefdp.pterodactylpanel.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()

    private val _events = MutableSharedFlow<WSMessage>()
    val events: SharedFlow<WSMessage> = _events.asSharedFlow()

    var onTokenRequired: (() -> Unit)? = null

    fun connect(
        wsUrl:String,
        initialToken: String,
        origin: String // origin must be the panel url
    ) {
        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Origin", origin)
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                authenticate(initialToken)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch {
                    try {
                        val message = gson.fromJson(text, WSMessage::class.java)

                        handleMessage(message)
                    } catch(e: Exception) {
                        Logger.error("WebSocketManager", "Failed to parse message: $text", e)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Logger.error("WebSocketManager", "WebSocket failure", t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Logger.debug("WebSocketManager", "WebSocket closed: $code - $reason")
            }
        }

        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    private suspend fun handleMessage(message: WSMessage) {
        when (message.event) {
            WSEvents.TOKEN_EXPIRING, WSEvents.TOKEN_EXPIRED -> {
                Logger.warn("WebSocketManager", message.event.value)

                onTokenRequired?.invoke()
            }

            else -> {
                _events.emit(message)
            }
        }
    }

    fun authenticate(token: String) {
        sendEvent(
            WSEvents.AUTH,
            listOf(JsonPrimitive(token))
        )
    }

    fun requestStats() {
        sendEvent(
            WSEvents.SEND_STATS,
            null
        )
    }

    fun requestLogs() {
        sendEvent(
            WSEvents.SEND_LOGS,
            null
        )
    }

    fun sendCommand(command: String) {
        sendEvent(
            WSEvents.SEND_COMMAND,
            listOf(JsonPrimitive(command))
        )
    }

    fun setPowerState(state: ServerPowerSignal) {
        sendEvent(
            WSEvents.SET_STATE,
            listOf(JsonPrimitive(state.value))
        )
    }

    private fun sendEvent(event: WSEvents, args: List<JsonElement>?) {
        val message = WSMessage(event, args)
        val jsonString = gson.toJson(message)

        webSocket?.send(jsonString)
    }

    fun disconnect() {
        webSocket?.close(1000, "App closed connection")
        webSocket = null
    }
}