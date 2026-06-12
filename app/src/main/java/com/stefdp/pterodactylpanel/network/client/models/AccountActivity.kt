package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class AccountActivity(
    val `object`: String = "activity_log",
    val attributes: AccountActivityAttributes
)

data class AccountActivityAttributes(
    val id: String,
    val batch: String? = null,
    val event: String,
    @SerializedName("is_api") val isApi: Boolean,
    val ip: String,
    val description: String? = null,
    val properties: AccountActivityAttributesProperties,
    @SerializedName("has_additional_metadata") val hasAdditionalMetadata: Boolean,
    val timestamp: String
)

data class AccountActivityAttributesProperties(
    val identifier: String? = null,
    val ip: String? = null,
    @SerializedName("useragent") val userAgent: String? = null,
)
