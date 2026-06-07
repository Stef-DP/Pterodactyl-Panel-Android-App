package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName

data class AddClientSshKeyBody(
    val name: String,
    @SerializedName("public_key") val publicKey: String,
)
