package com.stefdp.pterodactylpanel.network.client.models.requests

data class RenameServerFilesBody(
    val root: String,
    val files: List<RenameServerFile>
)

data class RenameServerFile(
    val from: String,
    val to: String
)
