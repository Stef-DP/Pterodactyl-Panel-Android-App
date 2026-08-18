package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationServer(
    val `object`: String = "server",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        @SerializedName("external_id") val externalId: String? = null,
        val uuid: String,
        val identifier: String,
        val name: String,
        val description: String? = null,
        val status: Status?,
        val suspended: Boolean,
        val limits: Limits,
        @SerializedName("feature_limits") val featureLimits: FeatureLimits,
        val user: Long,
        val node: Long,
        val allocation: Long,
        val nest: Long,
        val egg: Long,
        val container: Container,
        @SerializedName("updated_at") val updatedAt: String? = null,
        @SerializedName("created_at") val createdAt: String,
        val relationships: Relationships? = null
    ) {
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

        data class Limits(
            val memory: Long,
            val swap: Long,
            val disk: Long,
            val io: Long,
            val cpu: Long,
            val threads: String? = null,
            @SerializedName("oom_disabled") val oomDisabled: Boolean
        )

        data class FeatureLimits(
            val databases: Long,
            val allocations: Long,
            val backups: Long
        )

        data class Container(
            @SerializedName("startup_command") val startupCommand: String,
            val image: String,
            val installed: Int,
            val environment: Map<String, Any?>
        )

        data class Relationships(
            val allocations: Allocations? = null,
            val user: ApplicationUser? = null,
            val subusers: Subusers? = null,
            val nest: ApplicationNest? = null,
            val egg: ApplicationEgg? = null,
            val variables: Variables? = null,
            val location: ApplicationLocation? = null,
            val node: ApplicationNode? = null,
            val databases: Databases? = null,
        ) {
            data class Allocations(
                val `object`: String = "list",
                val data: List<ApplicationAllocation>
            )

            data class Subusers(
                val `object`: String = "list",
                val data: List<ApplicationServerSubuser>
            )

            data class Variables(
                val `object`: String = "list",
                val data: List<ApplicationServerVariable>
            )

            data class Databases(
                val `object`: String = "list",
                val data: List<ApplicationServerDatabase>
            )
        }
    }
}