package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ServerBackup

data class GetServerBackupsResponse(
    val `object`: String = "list",
    val data: List<ServerBackup>,
    val meta: GetServerBackupsMeta
)

data class GetServerBackupsMeta(
    @SerializedName("backup_count") val backupCount: Long,
    val pagination: GetServerBackupsMetaPagination
)

data class GetServerBackupsMetaPagination(
    val total: Long,
    val count: Long,
    @SerializedName("per_page") val perPage: Long,
    @SerializedName("current_page") val currentPage: Long,
    @SerializedName("total_pages") val totalPages: Long,
    val links: GetServerBackupsMetaPaginationLinks
)

data class GetServerBackupsMetaPaginationLinks(
    val next: String? = null,
    val previous: String? = null
)