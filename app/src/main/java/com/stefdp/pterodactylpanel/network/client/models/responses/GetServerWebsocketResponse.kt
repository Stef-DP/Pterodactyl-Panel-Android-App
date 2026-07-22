package com.stefdp.pterodactylpanel.network.client.models.responses

data class GetServerWebsocketResponse(
    val data: Data
) {
    data class Data(
        val token: String,
        val socket: String
    )
}
