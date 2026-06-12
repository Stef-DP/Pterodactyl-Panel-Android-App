package com.stefdp.pterodactylpanel.network.client.models.requests

data class DeleteServerFilesBody(
    val root: String? = null,
    val files: List<String>
)
