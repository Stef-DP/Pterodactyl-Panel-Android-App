package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class SshKey(
    val `object`: String = "ssh_key",
    val attributes: Attributes
) {
    data class Attributes(
        val name: String,
        val fingerprint: String,
        @SerializedName("public_key") val publicKey: String,
        @SerializedName("created_at") val createdAt: String,
    )
}