package com.stefdp.pterodactylpanel.network.application.models.responses

import com.stefdp.pterodactylpanel.network.application.models.ApplicationServerDatabase

data class ListServerDatabasesResponse(
    val `object`: String = "list",
    val data: List<ApplicationServerDatabase>
)
