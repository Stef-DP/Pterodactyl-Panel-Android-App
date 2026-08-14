package com.stefdp.pterodactylpanel.network.node.models.responses

import com.google.gson.annotations.SerializedName

data class GetNodeSystemV1Response(
    val architecture: String,
    @SerializedName("cpu_count") val cpuCount: Int,
    @SerializedName("kernel_version") val kernelVersion: String,
    val os: String,
    val version: String,
)