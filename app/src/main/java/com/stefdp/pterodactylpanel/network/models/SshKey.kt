package com.stefdp.pterodactylpanel.network.models

import com.google.gson.annotations.SerializedName

data class SshKey(
    @SerializedName("object") val objectType: String = "ssh_key",
    val attributes: SshKeyAttributes
)

data class SshKeyAttributes(
    val name: String,
    val fingerprint: String,
    @SerializedName("public_key") val publicKey: String,
    @SerializedName("created_at") val createdAt: String,
)