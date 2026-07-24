package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask

data class UpdateServerScheduleTaskBody(
    val action: ServerScheduleTask.Attributes.Action,
    @SerializedName("time_offset") val timeOffset: Long,
    @SerializedName("continue_on_failure") val continueOnFailure: Boolean = false,
    val payload: String,
    @SerializedName("sequence_id") val sequenceId: Long? = null,
)
