package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationNode(
    val `object`: String = "node",
    val attributes: ApplicationNodeAttributes
)

data class ApplicationNodeAttributes(
    val id: Long,
    val uuid: String,
    val public: Boolean,
    val name: String,
    val description: String,
    @SerializedName("location_id") val locationId: Long,
    val fqdn: String,
    val scheme: ApplicationNodeScheme,
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
    @SerializedName("allocated_resources") val allocatedResources: ApplicationNodeAttributesAllocatedResources,
    val relationships: ApplicationNodeRelationships
)

data class ApplicationNodeAttributesAllocatedResources(
    val memory: Long,
    val disk: Long
)

data class ApplicationNodeRelationships(
    val allocations: ApplicationNodeRelationshipsAllocations,
    val location: ApplicationLocation,
    val servers: ApplicationNodeRelationshipsServers
)

data class ApplicationNodeRelationshipsAllocations(
    val `object`: String = "list",
    val data: List<ApplicationAllocationAttributes>
)

data class ApplicationNodeRelationshipsServers(
    val `object`: String = "list",
    val data: List<ApplicationServer>
)

enum class ApplicationNodeScheme(val value: String) {
    @SerializedName("http")
    HTTP("http"),

    @SerializedName("https")
    HTTPS("https");

    override fun toString(): String = value
}