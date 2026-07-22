package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationNode(
    val `object`: String = "node",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        val uuid: String,
        val public: Boolean,
        val name: String,
        val description: String,
        @SerializedName("location_id") val locationId: Long,
        val fqdn: String,
        val scheme: Scheme,
        @SerializedName("behind_proxy") val behindProxy: Boolean,
        @SerializedName("maintenance_mode") val maintenanceMode: Boolean,
        val memory: Long,
        @SerializedName("memory_overallocate") val memoryOverallocate: Long,
        val disk: Long,
        @SerializedName("disk_overallocate") val diskOverallocate: Long,
        @SerializedName("upload_size") val uploadSize: Long,
        @SerializedName("daemon_listen") val daemonListen: Int,
        @SerializedName("daemon_sftp") val daemonSftp: Int,
        @SerializedName("daemon_base") val daemonBase: String,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
        @SerializedName("allocated_resources") val allocatedResources: AllocatedResources,
        val relationships: Relationships? = null
    ) {
        data class AllocatedResources(
            val memory: Long,
            val disk: Long
        )

        data class Relationships(
            val allocations: Allocations,
            val location: ApplicationLocation,
            val servers: Servers
        ) {
            data class Allocations(
                val `object`: String = "list",
                val data: List<ApplicationAllocation>
            )

            data class Servers(
                val `object`: String = "list",
                val data: List<ApplicationServer>
            )
        }

        enum class Scheme(val value: String) {
            @SerializedName("http")
            HTTP("http"),

            @SerializedName("https")
            HTTPS("https");

            override fun toString(): String = value
        }
    }
}