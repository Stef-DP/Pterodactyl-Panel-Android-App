package com.stefdp.pterodactylpanel.network.websocket.models

import com.google.gson.JsonElement

data class WSMessage(
    val event: WSEvents,
    val args: List<JsonElement>? = null
)
