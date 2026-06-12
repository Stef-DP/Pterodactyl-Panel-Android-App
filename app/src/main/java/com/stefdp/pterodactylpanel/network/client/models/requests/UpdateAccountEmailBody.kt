package com.stefdp.pterodactylpanel.network.client.models.requests

data class UpdateAccountEmailBody(
    val email: String,
    val password: String
)