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
    val relationships: ApplicationServerDatabaseRelationships? = null
)

data class ApplicationServerDatabaseRelationships(
    val host: ApplicationServerDatabaseRelationshipsHost? = null,
    val password: ApplicationServerDatabaseRelationshipsPassword? = null
)

data class ApplicationServerDatabaseRelationshipsHost(
    val `object`: String = "database_host",
    val attributes: ApplicationServerDatabaseRelationshipsHostAttributes
)

data class ApplicationServerDatabaseRelationshipsHostAttributes(
    val id: Long,
    val name: String,
    val host: String,
    val port: Int,
    val username: String,
    val node: Long,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ApplicationServerDatabaseRelationshipsPassword(
    val `object`: String = "database_password",
    val attributes: ApplicationServerDatabaseRelationshipsPasswordAttributes
)

data class ApplicationServerDatabaseRelationshipsPasswordAttributes(
    val password: String
)