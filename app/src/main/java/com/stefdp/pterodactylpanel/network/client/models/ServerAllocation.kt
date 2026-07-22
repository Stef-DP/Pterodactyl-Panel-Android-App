package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerAllocation(
    val `object`: String = "allocation",
    val attributes: Attributes,
) {
    data class Attributes(
        val id: Long,
        val ip: String,
        @SerializedName("ip_alias") val ipAlias: String? = null,
        val port: Int,
        val notes: String? = null,
        @SerializedName("is_default") val isDefault: Boolean,
    )
}
