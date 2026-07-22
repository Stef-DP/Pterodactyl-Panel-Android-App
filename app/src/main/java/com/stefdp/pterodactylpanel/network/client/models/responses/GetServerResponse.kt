package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.Server

data class GetServerResponse(
    val `object`: String = "server",
    val attributes: Server.Attributes,
    val meta: Meta
) {
    data class Meta(
        @SerializedName("is_server_owner") val isServerOwner: Boolean,
        @SerializedName("user_permissions") val userPermissions: List<String>
    )
}