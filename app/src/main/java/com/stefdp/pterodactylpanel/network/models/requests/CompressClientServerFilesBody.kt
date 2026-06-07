package com.stefdp.pterodactylpanel.network.models.requests

data class CompressClientServerFilesBody(
    val root: String? = null,
    val files: List<String>
)
