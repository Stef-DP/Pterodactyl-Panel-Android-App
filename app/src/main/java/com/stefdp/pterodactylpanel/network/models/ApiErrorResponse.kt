package com.stefdp.pterodactylpanel.network.models

data class ApiErrorResponse(
    val errors: List<ApiError>
)

data class ApiError(
    val code: String,
    val status: String? = null,
    val detail: String,
    val source: Map<String, String>? = null
)
