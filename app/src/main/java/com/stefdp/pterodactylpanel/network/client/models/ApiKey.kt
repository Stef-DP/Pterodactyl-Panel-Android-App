package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ApiKey(
    val `object`: String = "api_key",
    val attributes: Attributes
) {
    data class Attributes(
        val identifier: String,
        val description: String,
        @SerializedName("allowed_ips") val allowedIps: List<String>,
        @SerializedName("last_used_at") val lastUsedAt: String? = null,
        @SerializedName("created_at") val createdAt: String,
    )
}