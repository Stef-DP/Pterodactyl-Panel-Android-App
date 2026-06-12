package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerStats(
    val `object`: String = "stats",
    val attributes: ServerStatsAttributes,
)

data class ServerStatsAttributes(
    @SerializedName("current_state") val currentState: String,
    @SerializedName("is_suspended") val isSuspended: Boolean,
    val resources: ServerStatsResources,
)

data class ServerStatsResources(
    @SerializedName("memory_bytes") val memoryBytes: Long,
    @SerializedName("cpu_absolute") val cpuAbsolute: Double,
    @SerializedName("disk_bytes") val diskBytes: Long,
    @SerializedName("network_rx_bytes") val networkRxBytes: Long,
    @SerializedName("network_tx_bytes") val networkTxBytes: Long,
)
