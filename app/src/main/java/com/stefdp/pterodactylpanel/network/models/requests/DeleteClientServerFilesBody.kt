package com.stefdp.pterodactylpanel.network.models.requests

data class DeleteClientServerFilesBody(
    val root: String? = null,
    val files: List<String>
)
