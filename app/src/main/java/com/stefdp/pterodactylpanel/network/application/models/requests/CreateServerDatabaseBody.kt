package com.stefdp.pterodactylpanel.network.application.models.requests

data class CreateServerDatabaseBody(
    val database: String,
    val host: Long,
    val remote: String
)
