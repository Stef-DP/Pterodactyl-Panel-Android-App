package com.stefdp.pterodactylpanel.network.client.models.requests

data class UpdateServerFilesPermissionsBody(
    val files: List<UpdateServerFilePermissionsFile>,
    val root: String? = null
)

data class UpdateServerFilePermissionsFile(
    val file: String,
    val mode: String // octal like 755 etc.
)
