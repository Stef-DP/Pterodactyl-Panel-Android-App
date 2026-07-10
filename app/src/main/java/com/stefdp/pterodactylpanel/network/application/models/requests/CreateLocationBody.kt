package com.stefdp.pterodactylpanel.network.application.models.requests

data class CreateLocationBody(
    val short: String,
    val long: String? = null
)
