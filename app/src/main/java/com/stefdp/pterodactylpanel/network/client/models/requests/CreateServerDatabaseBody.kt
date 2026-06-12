package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

data class CreateServerDatabaseBody(
    @SerializedName("database") val databaseName: String,
    @SerializedName("remote") val allowedIp: String = "%"
)
