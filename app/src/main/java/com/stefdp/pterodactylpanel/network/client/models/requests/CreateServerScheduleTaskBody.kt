package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

data class CreateServerScheduleTaskBody(
    val action: com.stefdp.pterodactylpanel.network.client.models.ServerScheduleAction,
    val payload: String,
    @SerializedName("time_offset") val timeOffset: Long,
    @SerializedName("continue_on_failure") val continueOnFailure: Boolean = false,
)
