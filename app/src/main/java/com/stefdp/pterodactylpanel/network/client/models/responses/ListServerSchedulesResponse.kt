package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ServerSchedule

data class ListServerSchedulesResponse(
    val `object`: String = "list",
    val data: List<ServerSchedule>
)
