package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerSchedule

data class GetClientServerSchedulesResponse(
    @SerializedName("object") val objectType: String = "list",
    val data: List<ServerSchedule>
)
