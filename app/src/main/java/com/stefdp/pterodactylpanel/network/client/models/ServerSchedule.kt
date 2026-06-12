package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerSchedule(
    val `object`: String = "server_schedule",
    val attributes: ServerScheduleAttributes
)

data class ServerScheduleAttributes(
    val id: Long,
    val name: String,
    val cron: ServerScheduleCron,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_processing") val isProcessing: Boolean,
    @SerializedName("last_run_at") val lastRunAt: String? = null,
    @SerializedName("next_run_at") val nextRunAt: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val relationships: ServerScheduleRelationships,
)

data class ServerScheduleCron(
    @SerializedName("day_of_week") val dayOfWeek: String,
    @SerializedName("day_of_month") val dayOfMonth: String,
    val month: String,
    val hour: String,
    val minute: String,
)

data class ServerScheduleRelationships(
    val tasks: ServerScheduleRelationshipsTasks,
)

data class ServerScheduleRelationshipsTasks(
    val `object`: String = "list",
    val data: List<ServerScheduleTask>,
)

enum class ServerScheduleAction(val value: String) {
    @SerializedName("command")
    COMMAND("command"),

    @SerializedName("power")
    POWER("power"),

    @SerializedName("backup")
    BACKUP("backup");

    override fun toString(): String = value
}