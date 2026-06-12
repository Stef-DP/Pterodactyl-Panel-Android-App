package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerBackup(
    val `object`: String = "backup",
    val attributes: ServerBackupAttributes
)

data class ServerBackupAttributes(
    val uuid: String,
    val name: String,
    @SerializedName("ignored_files") val ignoredFiles: List<String>,
    val checksum: String? = null,
    val bytes: Long,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("completed_at") val completedAt: String? = null,
    @SerializedName("is_successful") val isSuccessful: Boolean,
    @SerializedName("is_locked") val isLocked: Boolean,
)
