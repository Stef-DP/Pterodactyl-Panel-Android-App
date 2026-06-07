package com.stefdp.pterodactylpanel.network.models.requests

data class RenameClientServerFilesBody(
    val root: String,
    val files: List<RenameClientServerFile>
)

data class RenameClientServerFile(
    val from: String,
    val to: String
)
