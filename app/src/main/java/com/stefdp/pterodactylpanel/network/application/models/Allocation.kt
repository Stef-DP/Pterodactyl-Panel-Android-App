package com.stefdp.pterodactylpanel.network.application.models

data class ApplicationAllocation(
    val `object`: String = "allocation",
    val attributes: ApplicationAllocationAttributes
)

data class ApplicationAllocationAttributes(
    val id: Long,
    val ip: String,
    val alias: String? = null,
    val port: Int,
    val notes: String? = null,
    val assigned: Boolean,
    val relationships: ApplicationAllocationRelationships? = null
)

data class ApplicationAllocationRelationships(
    val node: ApplicationNode? = null,
    val server: ApplicationServer? = null
)