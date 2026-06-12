package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerBackup

data class GetClientServerBackupsResponse(
    val `object`: String = "list",
    val data: List<ServerBackup>,
    val meta: GetClientServerBackupsMeta
)

data class GetClientServerBackupsMeta(
    val pagination: GetClientServerBackupsMetaPagination
)

data class GetClientServerBackupsMetaPagination(
    val total: Long,
    val count: Long,
    @SerializedName("per_page") val perPage: Long,
    @SerializedName("current_page") val currentPage: Long,
    @SerializedName("total_pages") val totalPages: Long,
    val links: GetClientServerBackupsMetaPaginationLinks
)

data class GetClientServerBackupsMetaPaginationLinks(
    val next: String? = null,
    val previous: String? = null
)