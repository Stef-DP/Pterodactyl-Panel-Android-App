package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName

data class GetServerResponse(
    val `object`: String = "server",
    val attributes: com.stefdp.pterodactylpanel.network.client.models.ServerAttributes,
    val meta: GetServerMeta
)

data class GetServerMeta(
    @SerializedName("is_server_owner") val isServerOwner: Boolean,
    @SerializedName("user_permissions") val userPermissions: List<String>
)