package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ServerDatabase

data class ListServerDatabasesResponse(
    val `object`: String = "list",
    val data: List<ServerDatabase>
)
