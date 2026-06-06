package com.stefdp.pterodactylpanel.network.models.requests

data class UpdateClientEmailBody(
    val email: String,
    val password: String
)