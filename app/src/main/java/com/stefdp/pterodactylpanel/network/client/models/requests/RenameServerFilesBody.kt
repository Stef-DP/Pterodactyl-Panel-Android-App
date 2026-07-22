package com.stefdp.pterodactylpanel.network.client.models.requests

data class RenameServerFilesBody(
    val root: String,
    val files: List<File>
) {
    data class File(
        val from: String,
        val to: String
    )
}
