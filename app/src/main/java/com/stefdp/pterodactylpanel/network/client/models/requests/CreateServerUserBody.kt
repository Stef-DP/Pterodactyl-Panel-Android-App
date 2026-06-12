package com.stefdp.pterodactylpanel.network.client.models.requests

data class CreateServerUserBody(
    val email: String,
    val permissions: List<String>
)
