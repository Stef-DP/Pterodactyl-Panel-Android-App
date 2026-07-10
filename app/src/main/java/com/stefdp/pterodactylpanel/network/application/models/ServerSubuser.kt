package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationServerSubuser(
    val `object`: String = "subuser",
    val attributes: ApplicationServerSubuserAttributes
)

data class ApplicationServerSubuserAttributes(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("server_id") val serverId: Long,
    val permissions: List<String>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null,
)
