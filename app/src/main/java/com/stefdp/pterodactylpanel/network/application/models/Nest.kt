package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationNest(
    val `object`: String = "nest",
    val attributes: ApplicationNestAttributes
)

data class ApplicationNestAttributes(
    val id: Long,
    val uuid: String,
    val author: String,
    val name: String,
    val description: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null
)