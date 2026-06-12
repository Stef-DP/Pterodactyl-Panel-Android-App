package com.stefdp.pterodactylpanel.network.client.models.requests

data class EnableAccount2FABody(
    val code: String,
    val password: String
)