package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ApiKeyAttributes

data class CreateAccountApiKeyResponse(
    val `object`: String = "api_key",
    val attributes: ApiKeyAttributes,
    val meta: CreateAccountApiKeyMeta,
)

data class CreateAccountApiKeyMeta(
    @SerializedName("secret_token") val secretToken: String
)