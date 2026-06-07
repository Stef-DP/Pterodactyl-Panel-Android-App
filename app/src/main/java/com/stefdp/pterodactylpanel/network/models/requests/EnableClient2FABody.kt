package com.stefdp.pterodactylpanel.network.models.requests

data class EnableClient2FABody(
    val code: String,
    val password: String
)