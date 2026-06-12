package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

data class AddAccountSshKeyBody(
    val name: String,
    @SerializedName("public_key") val publicKey: String,
)
