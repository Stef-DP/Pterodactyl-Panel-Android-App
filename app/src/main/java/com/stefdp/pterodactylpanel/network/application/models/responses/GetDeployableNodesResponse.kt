package com.stefdp.pterodactylpanel.network.application.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode

data class GetDeployableNodesResponse(
    val `object`: String = "list",
    val data: List<ApplicationNode>,
//    val meta: GetDeployableNodesResponseMeta
)

//data class GetDeployableNodesResponseMeta(
//    val pagination: GetDeployableNodesResponseMetaPagination
//)
//
//data class GetDeployableNodesResponseMetaPagination(
//    val total: Long,
//    val count: Long,
//    @SerializedName("per_page") val perPage: Long,
//    @SerializedName("current_page") val currentPage: Long,
//    @SerializedName("total_pages") val totalPages: Long,
//    val links: ListUsersResponseMetaPaginationLinks
//)
//
//data class GetDeployableNodesResponseMetaPaginationLinks(
//    val next: String? = null,
//    val previous: String? = null
//)
