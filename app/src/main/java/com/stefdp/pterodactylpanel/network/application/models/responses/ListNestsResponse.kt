package com.stefdp.pterodactylpanel.network.application.models.responses

import com.stefdp.pterodactylpanel.network.application.models.ApplicationNest

data class ListNestsResponse(
    val `object`: String = "list",
    val data: List<ApplicationNest>
)
