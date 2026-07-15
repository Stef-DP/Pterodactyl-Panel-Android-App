package com.stefdp.pterodactylpanel.network.websocket

import okhttp3.OkHttpClient

object WebSocket {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    val wsManager: WebSocketManager by lazy {
        WebSocketManager(okHttpClient)
    }
}