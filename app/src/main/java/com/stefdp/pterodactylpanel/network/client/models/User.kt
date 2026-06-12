package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class User(
    val `object`: String = "user",
    val attributes: UserAttributes
)

data class UserAttributes(
    val id: Long,
    val admin: Boolean,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val language: String
)