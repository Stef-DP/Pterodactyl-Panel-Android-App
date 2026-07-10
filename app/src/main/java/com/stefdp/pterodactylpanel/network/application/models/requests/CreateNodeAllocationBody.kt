package com.stefdp.pterodactylpanel.network.application.models.requests

data class CreateNodeAllocationBody(
    val ip: String,
    val ports: List<String>, // "1234" or "1234-1237"
    val alias: String? = null,
)
