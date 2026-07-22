package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationLocation(
    val `object`: String = "location",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        val short: String,
        val long: String? = null,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
        val relationships: Relationships? = null
    ) {
        data class Relationships(
            val nodes: Nodes? = null,
            val servers: Servers? = null
        ) {
            data class Nodes(
                val `object`: String = "list",
                val data: List<ApplicationNode>
            )

            data class Servers(
                val `object`: String = "list",
                val data: List<ApplicationServer>
            )
        }
    }
}