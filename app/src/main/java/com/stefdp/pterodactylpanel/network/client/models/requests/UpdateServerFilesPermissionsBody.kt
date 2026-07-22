package com.stefdp.pterodactylpanel.network.client.models.requests

data class UpdateServerFilesPermissionsBody(
    val files: List<File>,
    val root: String? = null
) {
    data class File(
        val file: String,
        val mode: String // octal like 755 etc.
    )
}
