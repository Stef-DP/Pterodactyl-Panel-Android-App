package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationServerDatabase(
    val `object`: String = "databases",
    val attributes: ApplicationServerDatabaseAttributes
)

data class ApplicationServerDatabaseAttributes(
    val id: Long,
    val server: Long,
    val host: Long,
    val database: String,
    val username: String,
    val remote: String,
    @SerializedName("max_connections") val maxConnections: Long,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null,
)
