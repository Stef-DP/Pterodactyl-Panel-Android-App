package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerScheduleTask(
    val `object`: String = "schedule_task",
    val attributes: Attributes,
) {
    data class Attributes(
        val id: Long,
        @SerializedName("sequence_id") val sequenceId: Long,
        val action: String,
        val payload: String,
        @SerializedName("time_offset") val timeOffset: Long,
        @SerializedName("is_queued") val isQueued: Boolean,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
    )
}