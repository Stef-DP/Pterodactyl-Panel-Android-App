package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName

data class CreateClientServerDatabaseBody(
    @SerializedName("database") val databaseName: String,
    @SerializedName("remote") val allowedIp: String = "%"
)
