package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask

data class CreateServerScheduleTaskBody(
    val action: ServerScheduleTask.Attributes.Action,
    val payload: String,
    @SerializedName("time_offset") val timeOffset: Long,
    @SerializedName("sequence_id") val sequenceId: Long? = null,
    @SerializedName("continue_on_failure") val continueOnFailure: Boolean = false,
)
