package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser

data class ListServerSubusersResponse(
    val `object`: String = "list",
    val data: List<ServerSubuser>
)
