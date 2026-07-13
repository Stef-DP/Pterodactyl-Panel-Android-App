package com.stefdp.pterodactylpanel.network.client.models.requests

data class CreateServerSubuserBody(
    val email: String,
    val permissions: List<String>
)
