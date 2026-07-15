package com.stefdp.pterodactylpanel.network.websocket.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ServerState

data class WebSocketStats(
    @SerializedName("memory_bytes") val memoryBytes: Long,
    @SerializedName("memory_limit_bytes") val memoryLimitBytes: Long,
    @SerializedName("cpu_absolute") val cpuAbsolute: Double,
    val network: WebSocketStatsNetwork,
    val uptime: Long,
    val state: ServerState,
    @SerializedName("disk_bytes") val diskBytes: Long
)

data class WebSocketStatsNetwork(
    @SerializedName("rx_bytes") val rxBytes: Long,
    @SerializedName("tx_bytes") val txBytes: Long,
)