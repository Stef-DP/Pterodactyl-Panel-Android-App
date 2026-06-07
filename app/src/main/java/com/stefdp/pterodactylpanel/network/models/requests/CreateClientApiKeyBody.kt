package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName

data class CreateClientApiKeyBody(
    val description: String,
    @SerializedName("allowed_ips") val allowedIps: List<String>? = null
)