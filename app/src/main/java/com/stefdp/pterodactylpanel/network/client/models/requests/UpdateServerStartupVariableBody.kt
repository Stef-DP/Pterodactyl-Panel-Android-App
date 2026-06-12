package com.stefdp.pterodactylpanel.network.client.models.requests

data class UpdateServerStartupVariableBody(
    val key: String,
    val value: String? = null
)
