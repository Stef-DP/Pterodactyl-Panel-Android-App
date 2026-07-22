package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ServerEggVariable

data class GetServerStartupVariablesResponse(
    val `object`: String = "list",
    val data: List<ServerEggVariable>,
    val meta: Meta
) {
    data class Meta(
        @SerializedName("startup_command") val startupCommand: String,
        @SerializedName("docker_images") val dockerImages: Map<String, String>,
        @SerializedName("raw_startup_command") val rawStartupCommand: String
    )
}