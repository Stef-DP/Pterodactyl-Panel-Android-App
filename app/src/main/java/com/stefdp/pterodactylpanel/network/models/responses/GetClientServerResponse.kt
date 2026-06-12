package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerAttributes

data class GetClientServerResponse(
    val `object`: String = "server",
    val attributes: ServerAttributes,
    val meta: GetClientServerMeta
)

data class GetClientServerMeta(
    @SerializedName("is_server_owner") val isServerOwner: Boolean,
    @SerializedName("user_permissions") val userPermissions: List<String>
)