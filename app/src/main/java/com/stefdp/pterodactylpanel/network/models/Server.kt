package com.stefdp.pterodactylpanel.network.models

import com.google.gson.annotations.SerializedName

data class Server(
    @SerializedName("object") val objectType: String = "server",
    val attributes: ServerAttributes
)

data class ServerAttributes(
    @SerializedName("server_owner") val serverOwner: Boolean,
    val identifier: String,
    val uuid: String,
    val name: String,
    val node: String,
    @SerializedName("sftp_details") val sftpDetails: ServerSftpDetails,
    val description: String,
    val limits: ServerLimits,
    @SerializedName("feature_limits") val featureLimits: ServerFeatureLimits,
    @SerializedName("is_suspended") val isSuspended: Boolean,
    @SerializedName("is_installing") val isInstalling: Boolean,
    val relationships: ServerRelationships,
)

data class ServerSftpDetails(
    val ip: String,
    val port: Int
)

data class ServerLimits(
    val memory: Int,
    val swap: Int,
    val disk: Int,
    val io: Int,
    val cpu: Int
)

data class ServerFeatureLimits(
    val databases: Int,
    val allocations: Int,
    val backups: Int
)

data class ServerRelationships(
    val allocations: ServerRelationShipsAllocations,
)

data class ServerRelationShipsAllocations(
    val objectType: String = "list",
    val data: List<ServerRelationshipsAllocation>
)

data class ServerRelationshipsAllocation(
    @SerializedName("object") val objectType: String = "allocation",
    val attributes: ServerRelationshipsAllocationAttributes
)

data class ServerRelationshipsAllocationAttributes(
    val id: Long,
    val ip: String,
    @SerializedName("ip_alias") val ipAlias: String? = null,
    val port: Int,
    val notes: String,
    @SerializedName("is_default") val isDefault: Boolean,
)