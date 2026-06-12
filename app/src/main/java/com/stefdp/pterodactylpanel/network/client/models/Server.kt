package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class Server(
    val `object`: String = "server",
    val attributes: ServerAttributes
)

data class ServerAttributes(
    @SerializedName("server_owner") val serverOwner: Boolean,
    val identifier: String,
    @SerializedName("__deprecated_uuid_short") val deprecatedUuidShort: String,
    @SerializedName("server:identifier") val serverIdentifier: String,
    @SerializedName("internal_id") val internalId: Long,
    val uuid: String,
    val name: String,
    val node: String,
    @SerializedName("is_node_under_maintenance") val isNodeUnderMaintenance: Boolean,
    @SerializedName("sftp_details") val sftpDetails: ServerSftpDetails,
    val description: String,
    val limits: ServerLimits,
    val invocation: String,
    @SerializedName("docker_image") val dockerImage: String,
    @SerializedName("egg_features") val eggFeatures: List<String>,
    @SerializedName("feature_limits") val featureLimits: ServerFeatureLimits,
    @SerializedName("is_suspended") val isSuspended: Boolean,
    @SerializedName("is_installing") val isInstalling: Boolean,
    @SerializedName("is_transferring") val isTransferring: Boolean,
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
    val cpu: Int,
    val threads: Int? = null,
    @SerializedName("oom_disabled") val oomDisabled: Boolean,
)

data class ServerFeatureLimits(
    val databases: Int,
    val allocations: Int,
    val backups: Int
)

data class ServerRelationships(
    val allocations: ServerRelationShipsAllocations,
    val variables: ServerRelationshipsVariables,
)

data class ServerRelationShipsAllocations(
    val `object`: String = "list",
    val data: List<ServerRelationshipsAllocation>
)

data class ServerRelationshipsAllocation(
    val `object`: String = "allocation",
    val attributes: ServerRelationshipsAllocationAttributes
)

data class ServerRelationshipsAllocationAttributes(
    val id: Long,
    val ip: String,
    @SerializedName("ip_alias") val ipAlias: String? = null,
    val port: Int,
    val notes: String? = null,
    @SerializedName("is_default") val isDefault: Boolean,
)

data class ServerRelationshipsVariables(
    val `object`: String = "list",
    val data: List<ServerEggVariable>
)