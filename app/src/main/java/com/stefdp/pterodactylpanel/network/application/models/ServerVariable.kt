package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationServerVariable(
    val `object`: String = "server_variable",
    val attributes: ApplicationServerVariableAttributes
)

data class ApplicationServerVariableAttributes(
    val id: Long,
    @SerializedName("egg_id") val eggId: Long,
    val name: String,
    val description: String,
    @SerializedName("env_variable") val envVariable: String,
    @SerializedName("default_value") val defaultValue: String,
    @SerializedName("user_viewable") val userViewable: Boolean,
    @SerializedName("user_editable") val userEditable: Boolean,
    val rules: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("server_value") val serverValue: String
)