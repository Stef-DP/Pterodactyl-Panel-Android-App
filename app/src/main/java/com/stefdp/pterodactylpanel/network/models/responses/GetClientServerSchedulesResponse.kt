package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerSchedule

data class GetClientServerSchedulesResponse(
    val `object`: String = "list",
    val data: List<ServerSchedule>
)
