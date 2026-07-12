package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

data class UpdateServerDetailsBody(
    val name: String? = null,
    val user: Long? = null,
    @SerializedName("external_id") val externalId: String? = null,
    val description: String? = null,
)
