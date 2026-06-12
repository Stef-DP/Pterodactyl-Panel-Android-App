package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerEggVariable

data class GetClientServerStartupVariablesResponse(
    val `object`: String = "list",
    val data: List<ServerEggVariable>,
    val meta: GetClientServerStartupVariablesMeta
)

data class GetClientServerStartupVariablesMeta(
    @SerializedName("startup_command") val startupCommand: String,
    @SerializedName("docker_images") val dockerImages: Map<String, String>,
    @SerializedName("raw_startup_command") val rawStartupCommand: String
)
