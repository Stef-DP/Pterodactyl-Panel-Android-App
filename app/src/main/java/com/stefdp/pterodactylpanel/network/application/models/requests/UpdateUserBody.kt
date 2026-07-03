package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

data class UpdateUserBody(
    val email: String? = null,
    val username: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    val password: String? = null,
    val language: String? = null,
    @SerializedName("root_admin") val rootAdmin: Boolean? = null,
    @SerializedName("external_id") val externalId: String? = null
)