package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationUser(
    val `object`: String = "user",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        @SerializedName("external_id") val externalId: String? = null,
        val uuid: String,val username: String,
        val email: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        val language: String,
        @SerializedName("root_admin") val rootAdmin: Boolean,
        @SerializedName("2fa") val twoFactorAuthentication: Boolean,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
        val relationships: Relationships? = null
    ) {
        data class Relationships(
            val servers: Servers?
        ) {
            data class Servers(
                val `object`: String = "list",
                val data: List<ApplicationServer>
            )
        }
    }
}