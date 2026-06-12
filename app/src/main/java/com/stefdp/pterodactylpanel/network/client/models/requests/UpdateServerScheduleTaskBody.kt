package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

data class UpdateServerScheduleTaskBody(
    val action: com.stefdp.pterodactylpanel.network.client.models.ServerScheduleAction,
    @SerializedName("time_offset") val timeOffset: Long,
    @SerializedName("continue_on_failure") val continueOnFailure: Boolean = false,
    val payload: String,
    @SerializedName("sequence_id") val sequenceId: Long? = null,
)
