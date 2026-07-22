package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ApiKey

data class CreateAccountApiKeyResponse(
    val `object`: String = "api_key",
    val attributes: ApiKey.Attributes,
    val meta: Meta,
) {
    data class Meta(
        @SerializedName("secret_token") val secretToken: String
    )
}