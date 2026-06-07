package com.stefdp.pterodactylpanel.network.models.requests

data class CreateClientServerFolderBody(
    val root: String? = null,
    val name: String
)
