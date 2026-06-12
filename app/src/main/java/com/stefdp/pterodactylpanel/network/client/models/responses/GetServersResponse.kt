package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.Server

data class GetServersResponse(
    val `object`: String = "list",
    val data: List<Server>,
    val meta: GetServersMeta
)

data class GetServersMeta(
    val pagination: GetServersMetaPagination
)

data class GetServersMetaPagination(
    val total: Long,
    val count: Long,
    @SerializedName("per_page") val perPage: Long,
    @SerializedName("current_page") val currentPage: Long,
    @SerializedName("total_pages") val totalPages: Long,
    val links: GetServersMetaPaginationLinks
)

data class GetServersMetaPaginationLinks(
    val next: String? = null,
    val previous: String? = null
)