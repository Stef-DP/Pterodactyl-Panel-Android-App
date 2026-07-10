package com.stefdp.pterodactylpanel.network.application.models.responses

import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg

data class ListNestEggsResponse(
    val `object`: String = "list",
    val data: List<ApplicationEgg>
)