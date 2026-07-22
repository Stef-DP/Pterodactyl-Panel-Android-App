package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode

data class CreateNodeBody(
    @SerializedName("daemon_listen") val daemonListen: Int,
    @SerializedName("daemon_sftp") val daemonSftp: Int,
    val disk: Long,
    @SerializedName("disk_overallocate") val diskOverallocate: Long,
    val fqdn: String,
    @SerializedName("location_id") val locationId: Long,
    val memory: Long,
    @SerializedName("memory_overallocate") val memoryOverallocate: Long,
    val name: String,
    val scheme: ApplicationNode.Attributes.Scheme,
    @SerializedName("behind_proxy") val behindProxy: Boolean? = null,
    @SerializedName("daemon_base") val daemonBase: String? = null,
    val description: String? = null,
    @SerializedName("maintenance_mode") val maintenanceMode: Boolean? = null,
    val public: Boolean? = null,
    @SerializedName("upload_size") val uploadSize: Long? = null
)

