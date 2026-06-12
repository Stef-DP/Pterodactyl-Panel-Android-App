package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

data class CreateAccountApiKeyBody(
    val description: String,
    @SerializedName("allowed_ips") val allowedIps: List<String>? = null
)