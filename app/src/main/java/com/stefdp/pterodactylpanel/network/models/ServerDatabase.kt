package com.stefdp.pterodactylpanel.network.models

import com.google.gson.annotations.SerializedName

data class ServerDatabase(
    @SerializedName("object") val objectType: String = "server_database",
    val attributes: ServerDatabaseAttributes
)

data class ServerDatabaseAttributes(
    val id: String,
    @SerializedName("server_id") val serverId: String,
    val host: ServerDatabaseAttributesHost,
    val name: String,
    val username: String,
    @SerializedName("connections_from") val allowedIp: String, // % means any
    @SerializedName("max_connections") val maxConnections: Long,
    val relationships: ServerDatabaseRelationships? = null
)

data class ServerDatabaseAttributesHost(
    val address: String,
    val port: Int
)

data class ServerDatabaseRelationships(
    val password: ServerDatabaseRelationshipsPassword
)

data class ServerDatabaseRelationshipsPassword(
    @SerializedName("object") val objectType: String = "database_password",
    val attributes: ServerDatabaseRelationshipsPasswordAttributes
)

data class ServerDatabaseRelationshipsPasswordAttributes(
    val password: String
)