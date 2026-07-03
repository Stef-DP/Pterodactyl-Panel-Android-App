package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNodeScheme

data class UpdateNodeBody(
    @SerializedName("daemon_listen") val daemonListen: Int? = null,
    @SerializedName("daemon_sftp") val daemonSftp: Int? = null,
    val disk: Long? = null,
    @SerializedName("disk_overallocate") val diskOverallocate: Long? = null,
    val fqdn: String? = null,
    @SerializedName("location_id") val locationId: Long? = null,
    val memory: Long? = null,
    @SerializedName("memory_overallocate") val memoryOverallocate: Long? = null,
    val name: String? = null,
    val scheme: ApplicationNodeScheme? = null,
    @SerializedName("behind_proxy") val behindProxy: Boolean? = null,
    @SerializedName("daemon_base") val daemonBase: String? = null,
    val description: String? = null,
    @SerializedName("maintenance_mode") val maintenanceMode: Boolean? = null,
    val public: Boolean? = null,
    @SerializedName("upload_size") val uploadSize: Long? = null
)
