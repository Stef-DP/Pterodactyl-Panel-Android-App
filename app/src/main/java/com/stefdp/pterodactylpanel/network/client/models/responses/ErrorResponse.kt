package com.stefdp.pterodactylpanel.network.client.models.responses

data class ErrorResponse(
    val errors: List<com.stefdp.pterodactylpanel.network.client.models.responses.PterodactylError>
)

data class PterodactylError(
    val code: String,
    val status: String? = null,
    val detail: String,
    val source: Map<String, String>? = null
)