package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class Server(
    val `object`: String = "server",
    val attributes: Attributes
) {
    data class Attributes(
        @SerializedName("server_owner") val serverOwner: Boolean,
        val identifier: String,
        @SerializedName("__deprecated_uuid_short") val deprecatedUuidShort: String,
        @SerializedName("server:identifier") val serverIdentifier: String,
        @SerializedName("internal_id") val internalId: Long,
        val uuid: String,
        val name: String,
        val node: String,
        @SerializedName("is_node_under_maintenance") val isNodeUnderMaintenance: Boolean,
        @SerializedName("sftp_details") val sftpDetails: SftpDetails,
        val description: String,
        val limits: Limits,
        val invocation: String,
        @SerializedName("docker_image") val dockerImage: String,
        @SerializedName("egg_features") val eggFeatures: List<String>,
        @SerializedName("feature_limits") val featureLimits: FeatureLimits,
        val status: Status? = null,
        @SerializedName("is_suspended") val isSuspended: Boolean,
        @SerializedName("is_installing") val isInstalling: Boolean,
        @SerializedName("is_transferring") val isTransferring: Boolean,
        val relationships: Relationships,
    ) {
        data class SftpDetails(
            val ip: String,
            val port: Int
        )

        data class Limits(
            val memory: Long,
            val swap: Long,
            val disk: Long,
            val io: Long,
            val cpu: Long,
            val threads: String? = null,
            @SerializedName("oom_disabled") val oomDisabled: Boolean,
        )

        data class FeatureLimits(
            val databases: Int,
            val allocations: Int,
            val backups: Int
        )

        enum class Status(val value: String) {
            @SerializedName("installing")
            INSTALLING("installing"),

            @SerializedName("install_failed")
            INSTALL_FAILED("install_failed"),

            @SerializedName("reinstall_failed")
            REINSTALL_FAILED("reinstall_failed"),

            @SerializedName("suspended")
            SUSPENDED("suspended"),

            @SerializedName("restoring_backup")
            RESTORING_BACKUP("restoring_backup");

            override fun toString(): String = value
        }

        data class Relationships(
            val allocations: Allocations,
            val variables: Variables,
        ) {
            data class Allocations(
                val `object`: String = "list",
                val data: List<ServerAllocation>
            )

            data class Variables(
                val `object`: String = "list",
                val data: List<ServerEggVariable>
            )
        }
    }
}