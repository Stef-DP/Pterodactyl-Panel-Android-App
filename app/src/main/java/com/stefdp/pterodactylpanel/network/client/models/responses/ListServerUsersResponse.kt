package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser

data class ListServerUsersResponse(
    val `object`: String = "list",
    val data: List<ServerSubuser>
)
