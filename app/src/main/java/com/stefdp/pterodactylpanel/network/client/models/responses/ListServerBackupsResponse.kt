package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ServerBackup

data class ListServerBackupsResponse(
    val `object`: String = "list",
    val data: List<ServerBackup>,
    val meta: Meta
) {
    data class Meta(
        @SerializedName("backup_count") val backupCount: Long,
        val pagination: Pagination
    ) {
        data class Pagination(
            val total: Long,
            val count: Long,
            @SerializedName("per_page") val perPage: Long,
            @SerializedName("current_page") val currentPage: Long,
            @SerializedName("total_pages") val totalPages: Long,
            val links: Links
        ) {
            data class Links(
                val next: String? = null,
                val previous: String? = null
            )
        }
    }
}