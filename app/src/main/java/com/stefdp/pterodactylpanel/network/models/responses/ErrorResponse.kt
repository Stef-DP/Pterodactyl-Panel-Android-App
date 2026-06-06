package com.stefdp.pterodactylpanel.network.models.responses

data class ErrorResponse(
    val errors: List<PterodactylError>
)

data class PterodactylError(
    val code: String,
    val status: String? = null,
    val detail: String,
    val source: Map<String, String>? = null
)