package com.stefdp.pterodactylpanel.network.models.requests

data class UpdateClientServerStartupVariableBody(
    val key: String,
    val value: String? = null
)
