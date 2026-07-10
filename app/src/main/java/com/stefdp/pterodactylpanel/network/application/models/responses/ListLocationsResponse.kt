package com.stefdp.pterodactylpanel.network.application.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.application.models.ApplicationLocation

data class ListLocationsResponse(
    val `object`: String = "list",
    val data: List<ApplicationLocation>,
    val meta: ListLocationsResponseMeta
)

data class ListLocationsResponseMeta(
    val pagination: ListLocationsResponseMetaPagination
)

data class ListLocationsResponseMetaPagination(
    val total: Long,
    val count: Long,
    @SerializedName("per_page") val perPage: Long,
    @SerializedName("current_page") val currentPage: Long,
    @SerializedName("total_pages") val totalPages: Long,
    val links: ListLocationsResponseMetaPaginationLinks
)

data class ListLocationsResponseMetaPaginationLinks(
    val next: String? = null,
    val previous: String? = null
)
