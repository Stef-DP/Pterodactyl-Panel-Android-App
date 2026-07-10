package com.stefdp.pterodactylpanel.network.application.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.application.models.ApplicationAllocation

data class ListNodeAllocationsResponse(
    val `object`: String = "list",
    val data: List<ApplicationAllocation>,
    val meta: ListNodeAllocationsResponseMeta
)

data class ListNodeAllocationsResponseMeta(
    val pagination: ListNodeAllocationsResponseMetaPagination
)

data class ListNodeAllocationsResponseMetaPagination(
    val total: Long,
    val count: Long,
    @SerializedName("per_page") val perPage: Long,
    @SerializedName("current_page") val currentPage: Long,
    @SerializedName("total_pages") val totalPages: Long,
    val links: ListNodeAllocationsResponseMetaPaginationLinks
)

data class ListNodeAllocationsResponseMetaPaginationLinks(
    val next: String? = null,
    val previous: String? = null
)
