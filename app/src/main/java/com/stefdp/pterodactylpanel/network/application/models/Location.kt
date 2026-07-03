package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationLocation(
    val `object`: String = "location",
    val attributes: ApplicationLocationAttributes
)

data class ApplicationLocationAttributes(
    val id: Long,
    val short: String,
    val long: String? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null
)