package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName

data class UpdateClientPasswordBody(
    @SerializedName("current_password") val currentPassword: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)