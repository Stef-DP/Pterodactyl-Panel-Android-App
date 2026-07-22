package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationServerDatabase(
    val `object`: String = "databases",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        val server: Long,
        val host: Long,
        val database: String,
        val username: String,
        val remote: String,
        @SerializedName("max_connections") val maxConnections: Long,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
        val relationships: Relationships? = null
    ) {
        data class Relationships(
            val host: Host? = null,
            val password: Password? = null
        ) {
            data class Host(
                val `object`: String = "database_host",
                val attributes: Attributes
            ) {
                data class Attributes(
                    val id: Long,
                    val name: String,
                    val host: String,
                    val port: Int,
                    val username: String,
                    val node: Long,
                    @SerializedName("created_at") val createdAt: String,
                    @SerializedName("updated_at") val updatedAt: String? = null
                )
            }

            data class Password(
                val `object`: String = "database_password",
                val attributes: Attributes
            ) {
                data class Attributes(
                    val password: String
                )
            }
        }
    }
}