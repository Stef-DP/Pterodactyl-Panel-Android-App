package com.stefdp.pterodactylpanel.network.models.requests

data class UpdateClientServerFilePermissionsBody(
    val files: List<UpdateClientServerFilePermissionsFile>,
    val root: String? = null
)

data class UpdateClientServerFilePermissionsFile(
    val file: String,
    val mode: String // octal like 755 etc.
)
