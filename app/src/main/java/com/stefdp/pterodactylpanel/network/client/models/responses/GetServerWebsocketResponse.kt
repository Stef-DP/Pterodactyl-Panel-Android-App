package com.stefdp.pterodactylpanel.network.client.models.responses

data class GetServerWebsocketResponse(
    val data: GetServerWebsocketResponseData
)

data class GetServerWebsocketResponseData(
    val token: String,
    val socket: String
)
