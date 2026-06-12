package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.Server

data class GetClientServersResponse(
    val `object`: String = "list",
    val data: List<Server>,
    val meta: GetClientServersMeta
)

data class GetClientServersMeta(
    val pagination: GetClientServersMetaPagination
)

data class GetClientServersMetaPagination(
    val total: Long,
    val count: Long,
    @SerializedName("per_page") val perPage: Long,
    @SerializedName("current_page") val currentPage: Long,
    @SerializedName("total_pages") val totalPages: Long,
    val links: GetClientServersMetaPaginationLinks
)

data class GetClientServersMetaPaginationLinks(
    val next: String? = null,
    val previous: String? = null
)