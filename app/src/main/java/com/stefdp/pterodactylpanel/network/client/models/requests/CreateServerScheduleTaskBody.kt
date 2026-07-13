package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleAction

data class CreateServerScheduleTaskBody(
    val action: ServerScheduleAction,
    val payload: String,
    @SerializedName("time_offset") val timeOffset: Long,
    @SerializedName("sequence_id") val sequenceId: Long? = null,
    @SerializedName("continue_on_failure") val continueOnFailure: Boolean = false,
)
