package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

data class CreateUserBody(
    val email: String,
    val username: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val password: String? = null,
    val language: String? = null,
    @SerializedName("root_admin") val rootAdmin: Boolean? = null,
    @SerializedName("external_id") val externalId: String? = null
)