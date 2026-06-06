package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ApiKeyAttributes

data class CreateAuthenticatedUserApiKeyResponse(
    @SerializedName("object") val objectType: String = "api_key",
    val attributes: ApiKeyAttributes,
    val meta: CreateAuthenticatedUserApiKeyMeta,
)

data class CreateAuthenticatedUserApiKeyMeta(
    @SerializedName("secret_token") val secretToken: String
)