package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerDatabase

data class ListClientServerDatabasesResponse(
    val `object`: String = "list",
    val data: List<ServerDatabase>
)
