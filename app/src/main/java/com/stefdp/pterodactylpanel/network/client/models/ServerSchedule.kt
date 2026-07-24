package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerSchedule(
    val `object`: String = "server_schedule",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        val name: String,
        val cron: Cron,
        @SerializedName("is_active") val isActive: Boolean,
        @SerializedName("is_processing") val isProcessing: Boolean,
        @SerializedName("only_when_online") val onlyWhenOnline: Boolean,
        @SerializedName("last_run_at") val lastRunAt: String? = null,
        @SerializedName("next_run_at") val nextRunAt: String,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
        val relationships: Relationships,
    ) {
        data class Cron(
            @SerializedName("day_of_week") val dayOfWeek: String,
            @SerializedName("day_of_month") val dayOfMonth: String,
            val month: String,
            val hour: String,
            val minute: String,
        )

        data class Relationships(
            val tasks: Tasks,
        ) {
            data class Tasks(
                val `object`: String = "list",
                val data: List<ServerScheduleTask>,
            )
        }
    }
}