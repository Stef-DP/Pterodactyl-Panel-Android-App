package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerScheduleAction

data class CreateClientServerScheduleTaskBody(
    val action: ServerScheduleAction,
    val payload: String,
    @SerializedName("time_offset") val timeOffset: Long,
    @SerializedName("continue_on_failure") val continueOnFailure: Boolean = false,
)
