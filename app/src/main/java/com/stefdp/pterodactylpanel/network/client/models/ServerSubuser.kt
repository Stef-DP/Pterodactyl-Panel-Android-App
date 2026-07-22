package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerSubuser(
    val `object`: String = "server_subuser",
    val attributes: Attributes
) {
    data class Attributes(
        val uuid: String,
        val username: String,
        val email: String,
        val image: String,
        @SerializedName("2fa_enabled") val twoFactorAuthenticationEnabled: Boolean,
        @SerializedName("created_at") val createdAt: String,
        val permissions: List<String>,
    )
}
