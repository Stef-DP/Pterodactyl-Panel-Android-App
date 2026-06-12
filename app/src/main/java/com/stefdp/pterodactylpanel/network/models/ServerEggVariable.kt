package com.stefdp.pterodactylpanel.network.models

import com.google.gson.annotations.SerializedName

data class ServerEggVariable(
    val `object`: String = "egg_variable",
    val attributes: ServerEggVariableAttributes
)

data class ServerEggVariableAttributes(
    val name: String,
    val description: String,
    @SerializedName("env_variable") val envVariable: String,
    @SerializedName("default_value") val defaultValue: String? = null,
    @SerializedName("server_value") val serverValue: String? = null,
    @SerializedName("is_editable") val isEditable: Boolean,
    val rules: String
)
