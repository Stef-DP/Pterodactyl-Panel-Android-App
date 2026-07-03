package com.stefdp.pterodactylpanel.network.application.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser

data class ListUsersResponse(
    val `object`: String = "list",
    val data: List<ApplicationUser>,
    val meta: ListUsersResponseMeta
)

data class ListUsersResponseMeta(
    val pagination: ListUsersResponseMetaPagination
)

data class ListUsersResponseMetaPagination(
    val total: Long,
    val count: Long,
    @SerializedName("per_page") val perPage: Long,
    @SerializedName("current_page") val currentPage: Long,
    @SerializedName("total_pages") val totalPages: Long,
    val links: ListUsersResponseMetaPaginationLinks
)

data class ListUsersResponseMetaPaginationLinks(
    val next: String? = null,
    val previous: String? = null
)