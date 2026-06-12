package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ServerSchedule

data class GetServerSchedulesResponse(
    val `object`: String = "list",
    val data: List<ServerSchedule>
)
