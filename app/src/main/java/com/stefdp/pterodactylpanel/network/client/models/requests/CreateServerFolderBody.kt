package com.stefdp.pterodactylpanel.network.client.models.requests

data class CreateServerFolderBody(
    val root: String? = null,
    val name: String
)
