package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerScheduleTask(
    val `object`: String = "schedule_task",
    val attributes: Attributes,
) {
    data class Attributes(
        val id: Long,
        @SerializedName("sequence_id") val sequenceId: Long,
        val action: Action,
        val payload: String,
        @SerializedName("time_offset") val timeOffset: Long,
        @SerializedName("is_queued") val isQueued: Boolean,
        @SerializedName("continue_on_failure") val continueOnFailure: Boolean,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
    ) {
        enum class Action(val value: String) {
            @SerializedName("command")
            COMMAND("command"),

            @SerializedName("power")
            POWER("power"),

            @SerializedName("backup")
            BACKUP("backup");

            override fun toString(): String = value
        }
    }
}