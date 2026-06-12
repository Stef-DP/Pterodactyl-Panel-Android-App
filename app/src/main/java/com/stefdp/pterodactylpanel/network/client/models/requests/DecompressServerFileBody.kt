package com.stefdp.pterodactylpanel.network.client.models.requests

data class DecompressServerFileBody(
    val root: String? = null,
    val file: String
)
