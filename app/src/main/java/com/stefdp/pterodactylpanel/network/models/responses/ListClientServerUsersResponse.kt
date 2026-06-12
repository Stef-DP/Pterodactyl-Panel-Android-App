package com.stefdp.pterodactylpanel.network.models.responses

import com.stefdp.pterodactylpanel.network.models.ServerSubuser

data class ListClientServerUsersResponse(
    val `object`: String = "list",
    val data: List<ServerSubuser>
)
