package com.stefdp.pterodactylpanel.network.models.requests

data class DecompressClientServerFileBody(
    val root: String? = null,
    val file: String
)
