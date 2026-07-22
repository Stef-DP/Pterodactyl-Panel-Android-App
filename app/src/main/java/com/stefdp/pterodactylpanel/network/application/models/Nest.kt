package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationNest(
    val `object`: String = "nest",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        val uuid: String,
        val author: String,
        val name: String,
        val description: String? = null,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
        val relationships: Relationships? = null
    ) {
        data class Relationships(
            val eggs: Eggs? = null,
            val servers: Servers? = null
        ) {
            data class Eggs(
                val `object`: String = "list",
                val data: List<ApplicationEgg>
            )

            data class Servers(
                val `object`: String = "list",
                val data: List<ApplicationServer>
            )
        }
    }
}