package com.stefdp.pterodactylpanel.network.client.models.requests

data class CompressServerFilesBody(
    val root: String? = null,
    val files: List<String>
)
