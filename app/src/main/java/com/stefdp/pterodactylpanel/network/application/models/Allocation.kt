package com.stefdp.pterodactylpanel.network.application.models

data class ApplicationAllocation(
    val `object`: String = "allocation",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        val ip: String,
        val alias: String? = null,
        val port: Int,
        val notes: String? = null,
        val assigned: Boolean,
        val relationships: Relationships? = null
    ) {
        data class Relationships(
            val node: ApplicationNode? = null,
            val server: ApplicationServer? = null
        )
    }
}