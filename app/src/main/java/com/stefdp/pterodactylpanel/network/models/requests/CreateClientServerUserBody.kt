package com.stefdp.pterodactylpanel.network.models.requests

data class CreateClientServerUserBody(
    val email: String,
    val permissions: List<String>
)
